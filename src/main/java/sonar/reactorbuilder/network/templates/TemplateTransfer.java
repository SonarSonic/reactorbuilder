package sonar.reactorbuilder.network.templates;

import net.minecraft.entity.player.EntityPlayer;
import sonar.reactorbuilder.common.ReactorBuilderTileEntity;
import sonar.reactorbuilder.common.dictionary.DictionaryEntry;
import sonar.reactorbuilder.common.dictionary.GlobalDictionary;
import sonar.reactorbuilder.common.reactors.templates.AbstractTemplate;

import javax.annotation.Nullable;

public class TemplateTransfer {

    public static final int MAX_PAYLOAD = 4096; ///4096 * 2(Short Byte Size)=8192 bytes = 8KB * 20 (ticks) = 160kbps required.

    public TransferStage stage;
    public AbstractTemplate template;

    public int index;

    @Nullable
    public EntityPlayer player;
    @Nullable
    public ReactorBuilderTileEntity dest;

    public TemplateTransfer(AbstractTemplate template, TransferStage stage){
        this.template = template;
        this.stage = stage;
    }

    public int getPayloadStart(){
        return index;
    }

    public int getPayloadEnd(){
        return Math.min(index + MAX_PAYLOAD, template.getIndexSize());
    }

    public boolean requiresPayloadPacket(){
        return index < template.getIndexSize();
    }

    public enum TransferStage {
        REQUESTED, HEADER_SENT, HEADER_ACK, PAYLOAD_SENT, PAYLOAD_ACK, FINISHED;
    }
}
