package sonar.reactorbuilder.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.reactorbuilder.ReactorBuilder;
import sonar.reactorbuilder.common.reactors.templates.AbstractTemplate;
import sonar.reactorbuilder.network.templates.TemplateManager;

public class PacketTemplateUploadPayload implements IMessage {

    public AbstractTemplate template;

    public int templateID;
    public int start;
    public int end;
    public ByteBuf byteBuf;

    public PacketTemplateUploadPayload() {}

    public PacketTemplateUploadPayload(AbstractTemplate template, int start, int end) {
        this.template = template;
        this.start = start;
        this.end = end;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(template.templateID);
        buf.writeInt(start);
        buf.writeInt(end);
        template.writePayloadToBuf(buf, start, end);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        templateID = buf.readInt();
        start = buf.readInt();
        end = buf.readInt();
        byteBuf = buf.retain();
    }

    public static class Handler implements IMessageHandler<PacketTemplateUploadPayload, IMessage> {
        @Override
        public IMessage onMessage(PacketTemplateUploadPayload message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return new PacketTemplateUploadPayloadAck(message.templateID);
        }

        private void handle(PacketTemplateUploadPayload message, MessageContext ctx) {
            TemplateManager.getDownloadHandler(ctx.side.isClient()).receivePayloadPacket(message);
        }
    }
}