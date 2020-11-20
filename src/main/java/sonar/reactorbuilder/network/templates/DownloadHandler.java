package sonar.reactorbuilder.network.templates;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import sonar.reactorbuilder.common.ReactorBuilderTileEntity;
import sonar.reactorbuilder.common.reactors.templates.AbstractTemplate;
import sonar.reactorbuilder.network.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DownloadHandler {

    public TemplateManager manager;

    public DownloadHandler(){}

    public void setManager(TemplateManager manager){
        this.manager = manager;
    }

    public List<Integer> requested = new ArrayList<>();

    public Map<Integer, TemplateTransfer> downloading = new HashMap<>();
    public Map<Integer, TemplateTransfer> uploading = new HashMap<>();

    public void clear(){
        requested.clear();
        downloading.clear();
        uploading.clear();
    }

    public void tick(){
        if(uploading.isEmpty()){
            return;
        }
        for(TemplateTransfer transfer : uploading.values()){
            switch (transfer.stage){
                case REQUESTED:
                    sendHeaderPacket(transfer);
                    break;
                case HEADER_SENT:
                    ///WAITING
                    break;
                case HEADER_ACK:
                    sendPayloadPacket(transfer);
                    break;
                case PAYLOAD_SENT:
                    ///WAITING
                    break;
                case PAYLOAD_ACK:
                    sendPayloadPacket(transfer);
                    break;
                case FINISHED:
                    ///TODO REMOVE ME
                    break;
            }
        }
    }

    //// REQUEST \\\\

    public void sendRequestPacket(int templateID){
        if(!requested.contains(templateID)){
            requested.add(templateID);
            PacketHandler.INSTANCE.sendToServer(new PacketTemplateRequest(templateID));
        }
    }

    public void receiveRequestPacket(int templateID, EntityPlayer player){
        AbstractTemplate template = manager.getTemplate(templateID);
        if(template != null){
            TemplateTransfer transfer = new TemplateTransfer(template, TemplateTransfer.TransferStage.REQUESTED);
            transfer.player = player;
            uploading.put(templateID, transfer);
        }
    }

    public void sendLocalTemplate(AbstractTemplate template, EntityPlayer player, ReactorBuilderTileEntity dest){
        TemplateTransfer transfer = new TemplateTransfer(template, TemplateTransfer.TransferStage.REQUESTED);
        transfer.player = player;
        transfer.dest = dest;
        uploading.put(template.templateID, transfer);
    }

    //// HEADER \\\\

    public void sendHeaderPacket(TemplateTransfer transfer){
        sendPacket(transfer, new PacketTemplateUploadHeader(transfer.template, transfer.dest != null ? transfer.dest.getPos() : null));
        transfer.stage = TemplateTransfer.TransferStage.HEADER_SENT;
    }

    public void receiveHeaderPacket(AbstractTemplate template, EntityPlayer player, @Nullable ReactorBuilderTileEntity dest){
        TemplateTransfer transfer = new TemplateTransfer(template, TemplateTransfer.TransferStage.HEADER_ACK);
        transfer.player = player;
        transfer.dest = dest;
        downloading.put(template.templateID, transfer);
    }

    public void headerPacketAck(PacketTemplateUploadHeaderAck message){
        TemplateTransfer template = getUpload(message.templateID);
        template.stage = TemplateTransfer.TransferStage.HEADER_ACK;
    }

    //// PAYLOAD \\\\

    public void sendPayloadPacket(TemplateTransfer transfer){
        int start = transfer.getPayloadStart();
        int end = transfer.getPayloadEnd();
        sendPacket(transfer, new PacketTemplateUploadPayload(transfer.template, start, end));
        transfer.stage = TemplateTransfer.TransferStage.PAYLOAD_SENT;
        transfer.index = end;
    }

    public void receivePayloadPacket(PacketTemplateUploadPayload message){
        TemplateTransfer template = getDownload(message.templateID);
        template.template.readPayloadFromBuf(message.byteBuf, message.start, message.end);
        template.index = message.end;

        if(!template.requiresPayloadPacket()){
            template.stage = TemplateTransfer.TransferStage.FINISHED;
            int templateID = manager.addTemplate(template.template);

            template.template.updateAdditionalInfo();
            template.template.sortAdditionalInfo();

            if(template.dest != null){
                template.dest.changeTemplate(templateID);
            }
            requested.remove(Integer.valueOf(templateID));
        }
    }

    public void payloadPacketAck(PacketTemplateUploadPayloadAck message){
        TemplateTransfer template = getUpload(message.templateID);

        if(template.requiresPayloadPacket()){
            template.stage = TemplateTransfer.TransferStage.PAYLOAD_ACK;
        }else{
            template.stage = TemplateTransfer.TransferStage.FINISHED;
        }
    }

    public TemplateTransfer getUpload(int templateID){
        return uploading.get(templateID);
    }


    public TemplateTransfer getDownload(int templateID){
        return downloading.get(templateID);
    }

    public abstract void sendPacket(TemplateTransfer transfer, IMessage packet);


    public static class Client extends DownloadHandler{

        public Client() {
            super();
        }

        @Override
        public void sendPacket(TemplateTransfer transfer, IMessage packet) {
            PacketHandler.INSTANCE.sendToServer(packet);
        }

    }

    public static class Server extends DownloadHandler{

        public Server() {
            super();
        }

        @Override
        public void sendPacket(TemplateTransfer transfer, IMessage packet) {
            PacketHandler.INSTANCE.sendTo(packet, (EntityPlayerMP)transfer.player);
        }
    }

}