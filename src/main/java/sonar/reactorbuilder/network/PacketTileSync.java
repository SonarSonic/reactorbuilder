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

public class PacketTileSync implements IMessage {

    public ReactorBuilderTileEntity te;
    public BlockPos builder;
    public EnumSyncPacket type;

    public ByteBuf byteBuf;

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(builder.toLong());
        buf.writeInt(type.ordinal());
        te.writeSyncPacket(buf, type);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        builder = BlockPos.fromLong(buf.readLong());
        type = EnumSyncPacket.values()[buf.readInt()];
        byteBuf = buf.retain();
    }

    public PacketTileSync() {}

    public PacketTileSync(ReactorBuilderTileEntity tileEntity, EnumSyncPacket type) {
        this.te = tileEntity;
        this.builder = tileEntity.getPos();
        this.type = type;
    }

    public static class Handler implements IMessageHandler<PacketTileSync, IMessage> {
        @Override
        public IMessage onMessage(PacketTileSync message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketTileSync message, MessageContext ctx) {
            EntityPlayer player = ReactorBuilder.proxy.getPlayer(ctx);
            TileEntity tileEntity = player.getEntityWorld().getTileEntity(message.builder);

            if(tileEntity instanceof ReactorBuilderTileEntity){
                ReactorBuilderTileEntity builder = (ReactorBuilderTileEntity) tileEntity;
                builder.readSyncPacket(message.byteBuf, message.type);
            }
        }
    }
}