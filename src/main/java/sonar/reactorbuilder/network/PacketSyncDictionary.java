package sonar.reactorbuilder.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.reactorbuilder.ReactorBuilder;
import sonar.reactorbuilder.common.dictionary.GlobalDictionary;
import sonar.reactorbuilder.common.dictionary.DictionaryEntry;

import java.util.HashMap;
import java.util.Map;

public class PacketSyncDictionary implements IMessage {

    public  Map<String, Integer> componentIds;

    public PacketSyncDictionary() {}

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(GlobalDictionary.GLOBAL_DICTIONARY.size());
        for(Map.Entry<String, DictionaryEntry> info : GlobalDictionary.GLOBAL_DICTIONARY.entrySet()){
            ByteBufUtils.writeUTF8String(buf, info.getKey());
            buf.writeInt(info.getValue().globalID);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        componentIds = new HashMap<>();
        int componentCount = buf.readInt();
        for(int i = 0; i < componentCount; i++){
            String refString = ByteBufUtils.readUTF8String(buf);
            int globalID = buf.readInt();
            componentIds.put(refString, globalID);
        }
    }

    public static class Handler implements IMessageHandler<PacketSyncDictionary, IMessage> {
        @Override
        public IMessage onMessage(PacketSyncDictionary message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketSyncDictionary message, MessageContext ctx) {

            for(Map.Entry<String, Integer> component : message.componentIds.entrySet()){
                DictionaryEntry componentInfo = GlobalDictionary.getComponentInfo(component.getKey());
                if(componentInfo == null){
                    ReactorBuilder.logger.error("Dictionary Sync: Missing component on client side REF: {} ID: {}", component.getKey(), component.getValue());
                    continue;
                }
                componentInfo.globalID = component.getValue();
            }

            ReactorBuilder.logger.info("Synced {} component types", message.componentIds.size());
        }
    }
}