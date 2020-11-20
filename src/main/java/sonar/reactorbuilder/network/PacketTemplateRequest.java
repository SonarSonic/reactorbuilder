package sonar.reactorbuilder.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.reactorbuilder.ReactorBuilder;
import sonar.reactorbuilder.network.templates.TemplateManager;

public class PacketTemplateRequest implements IMessage {

    public int templateID;

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(templateID);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        templateID = buf.readInt();
    }

    public PacketTemplateRequest() {}

    public PacketTemplateRequest(int templateID) {
        this.templateID = templateID;
    }

    public static class Handler implements IMessageHandler<PacketTemplateRequest, IMessage> {
        @Override
        public IMessage onMessage(PacketTemplateRequest message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketTemplateRequest message, MessageContext ctx) {
            EntityPlayer player = ReactorBuilder.proxy.getPlayer(ctx);
            TemplateManager.getDownloadHandler(ctx.side.isClient()).receiveRequestPacket(message.templateID, player);
        }
    }
}