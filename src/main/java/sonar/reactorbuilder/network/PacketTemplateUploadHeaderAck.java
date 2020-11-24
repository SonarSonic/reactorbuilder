package sonar.reactorbuilder.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.reactorbuilder.ReactorBuilder;
import sonar.reactorbuilder.network.templates.TemplateManager;
import sonar.reactorbuilder.network.templates.TemplateTransfer;

public class PacketTemplateUploadHeaderAck implements IMessage {

    public int templateID;

    public PacketTemplateUploadHeaderAck() {}

    public PacketTemplateUploadHeaderAck(int templateID) {
        this.templateID = templateID;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(templateID);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        templateID = buf.readInt();
    }

    public static class Handler implements IMessageHandler<PacketTemplateUploadHeaderAck, IMessage> {
        @Override
        public IMessage onMessage(PacketTemplateUploadHeaderAck message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketTemplateUploadHeaderAck message, MessageContext ctx) {
            TemplateManager.getDownloadHandler(ctx.side.isClient()).headerPacketAck(message);
        }
    }
}