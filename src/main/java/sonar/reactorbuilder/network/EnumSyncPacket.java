package sonar.reactorbuilder.network;

import net.minecraftforge.fml.relauncher.Side;

public enum EnumSyncPacket {

    //// SERVER TO CLIENT \\\\
    GENERAL_SYNC(Side.CLIENT),
    SYNC_TEMPLATE(Side.CLIENT),

    //// CLIENT TO SERVER \\\\
    TOGGLE_BUILDING(Side.SERVER),
    TOGGLE_DESTROYING(Side.SERVER),
    SYNC_CASING_TYPES(Side.SERVER);

    Side side;

    EnumSyncPacket(Side side) {
        this.side = side;
    }

    public Side getReceiver(){
        return side;
    }

    public Side getSender(){
        return side.isServer() ? Side.CLIENT : Side.SERVER;
    }

}
