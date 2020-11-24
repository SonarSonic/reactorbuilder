package sonar.reactorbuilder.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.reactorbuilder.ReactorBuilder;
import sonar.reactorbuilder.common.ReactorBuilderTileEntity;
import sonar.reactorbuilder.common.reactors.templates.AbstractTemplate;
import sonar.reactorbuilder.network.templates.TemplateManager;

import javax.annotation.Nullable;

public class PacketTemplateUploadHeader implements IMessage {

    public AbstractTemplate template;
    public BlockPos dest;

    public PacketTemplateUploadHeader() {}

    public PacketTemplateUploadHeader(AbstractTemplate template, @Nullable BlockPos dest) {
        this.template = template;
        this.dest = dest;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        AbstractTemplate.writeTemplateHeaderToByteBuf(buf, template);
        buf.writeBoolean(dest != null);
        if(dest != null){
            buf.writeLong(dest.toLong());
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        template = AbstractTemplate.readTemplateHeaderFromByteBuf(buf);
        if(buf.readBoolean()){
            dest = BlockPos.fromLong(buf.readLong());
        }
    }

    public static class Handler implements IMessageHandler<PacketTemplateUploadHeader, IMessage> {
        @Override
        public IMessage onMessage(PacketTemplateUploadHeader message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return new PacketTemplateUploadHeaderAck(message.template.templateID);
        }

        private void handle(PacketTemplateUploadHeader message, MessageContext ctx) {
            EntityPlayer player = ReactorBuilder.proxy.getPlayer(ctx);

            ReactorBuilderTileEntity builder = null;
            if(message.dest != null){
                TileEntity tileEntity = player.getEntityWorld().getTileEntity(message.dest);
                if(tileEntity instanceof ReactorBuilderTileEntity){
                    builder = (ReactorBuilderTileEntity) tileEntity;
                }
            }
            TemplateManager.getDownloadHandler(ctx.side.isClient()).receiveHeaderPacket(message.template, player, builder);
        }
    }
}