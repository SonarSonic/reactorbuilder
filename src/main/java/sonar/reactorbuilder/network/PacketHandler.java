package sonar.reactorbuilder.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class PacketHandler {
    private static int packetId = 0;

    public static SimpleNetworkWrapper INSTANCE = null;

    public PacketHandler() {}

    public static int nextID() {
        return packetId++;
    }

    public static void registerMessages(String channelName) {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
        registerMessages();
    }

    public static void registerMessages() {
        INSTANCE.registerMessage(PacketSyncDictionary.Handler.class, PacketSyncDictionary.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(PacketTileSync.Handler.class, PacketTileSync.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(PacketTileSync.Handler.class, PacketTileSync.class, nextID(), Side.SERVER);

        INSTANCE.registerMessage(PacketTemplateRequest.Handler.class, PacketTemplateRequest.class, nextID(), Side.SERVER);
        INSTANCE.registerMessage(PacketTemplateUploadHeader.Handler.class, PacketTemplateUploadHeader.class, nextID(), Side.SERVER);
        INSTANCE.registerMessage(PacketTemplateUploadHeader.Handler.class, PacketTemplateUploadHeader.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(PacketTemplateUploadHeaderAck.Handler.class, PacketTemplateUploadHeaderAck.class, nextID(), Side.SERVER);
        INSTANCE.registerMessage(PacketTemplateUploadHeaderAck.Handler.class, PacketTemplateUploadHeaderAck.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(PacketTemplateUploadPayload.Handler.class, PacketTemplateUploadPayload.class, nextID(), Side.SERVER);
        INSTANCE.registerMessage(PacketTemplateUploadPayload.Handler.class, PacketTemplateUploadPayload.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(PacketTemplateUploadPayloadAck.Handler.class, PacketTemplateUploadPayloadAck.class, nextID(), Side.SERVER);
        INSTANCE.registerMessage(PacketTemplateUploadPayloadAck.Handler.class, PacketTemplateUploadPayloadAck.class, nextID(), Side.CLIENT);
    }
}