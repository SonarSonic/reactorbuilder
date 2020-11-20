package sonar.reactorbuilder.common.reactors.templates;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import sonar.reactorbuilder.common.dictionary.DictionaryEntry;
import sonar.reactorbuilder.common.dictionary.DictionaryEntryType;
import sonar.reactorbuilder.common.dictionary.GlobalDictionary;
import sonar.reactorbuilder.common.reactors.TemplateType;
import sonar.reactorbuilder.util.Translate;

import javax.annotation.Nullable;
import java.util.Map;

public class UnderhaulSFRTemplate extends AbstractTemplate {

    public static DictionaryEntry casingSolid = GlobalDictionary.getComponentInfo("sfr_casing");
    public static DictionaryEntry casingGlass = GlobalDictionary.getComponentInfo("sfr_glass");

    @Nullable
    public DictionaryEntry fuel;
    public ItemStack edgeItem = ItemStack.EMPTY;
    private DictionaryEntry edges = null;

    public UnderhaulSFRTemplate(){}

    public UnderhaulSFRTemplate(String fileName, int xSize, int ySize, int zSize, @Nullable DictionaryEntry fuel) {
        super(fileName, xSize, ySize, zSize);
        this.fuel = fuel;
    }


    //// GENERAL \\\\

    @Override
    public TemplateType getTemplateType() {
        return TemplateType.UNDERHAUL_SFR;
    }

    @Nullable
    @Override
    public DictionaryEntry getComponent(int x, int y, int z){
        if(isComponent(x, y, z)){
            return blocks[x][y][z];
        }
        if(isCasing(x, y, z)){
            return isCasingGlass(x, y, z) ? casingGlass : casingSolid;
        }
        if(isEdge(x, y, z)){
            return edges;
        }
        return null;
    }


    //// BUILDING \\\\

    @Override
    public int getBuildPasses() {
        return 3;
    }

    @Override
    public String[] getBuildPassNames() {
        return new String[]{Translate.PASS_PLACING_COMPONENTS.t(), Translate.PASS_PLACING_CASINGS.t(), Translate.PASS_PLACING_EDGES.t()};
    }

    @Override
    public int getBuildPassTotal(int buildPass) {
        switch (buildPass){
            case 0: return totalSolidComponents;
            case 1: return totalSolidCasing + totalGlassCasing;
            case 2: return edges == null ? 0 : totalEdges;
        }
        return 0;
    }

    @Override
    public boolean canPlaceThisPass(int buildPass, int x, int y, int z, DictionaryEntry info) {
        switch (buildPass){
            case 0: return info.entryType == DictionaryEntryType.UNDERHAUL_COMPONENT;
            case 1: return info.entryType == DictionaryEntryType.UNDERHAUL_CASING_SOLID ||info.entryType == DictionaryEntryType.UNDERHAUL_CASING_GLASS;
            case 2: return info.entryType == DictionaryEntryType.UNDERHAUL_EDGES;
        }
        return false;
    }


    //// INFO \\\\

    @Override
    public DictionaryEntry getDefaultSolidCasing() {
        return casingSolid;
    }

    @Override
    public DictionaryEntry getDefaultGlassCasing() {
        return casingGlass;
    }

    @Override
    public void updateAdditionalInfo(){
        super.updateAdditionalInfo();
        if(totalSolidCasing != 0)
            required.put(casingSolid, totalSolidCasing);
        if(totalGlassCasing != 0)
            required.put(casingGlass, totalGlassCasing);

        edges = edgeItem.isEmpty() ? null : DictionaryEntry.makeEdgeComponent(edgeItem);

        if(edges != null){
            required.put(edges, totalEdges);
        }
    }

    @Override
    public void getStats(Map<String, String> statsMap) {
        statsMap.put(Translate.TEMPLATE_FILE_NAME.t(), fileName);
        statsMap.put(Translate.TEMPLATE_REACTOR_TYPE.t(), getTemplateType().fileType);
        statsMap.put(Translate.TEMPLATE_DIMENSIONS.t(), xSize + " x " + ySize + " x "  + zSize);

        if(fuel != null)
            statsMap.put(Translate.TEMPLATE_FUEL_TYPE.t(), fuel.getItemStack().getDisplayName());

        statsMap.put(Translate.TEMPLATE_COMPONENTS.t(), String.valueOf(totalSolidComponents));
        statsMap.put(Translate.TEMPLATE_CASING.t(), String.valueOf(totalSolidCasing + totalGlassCasing));
        statsMap.put(Translate.TEMPLATE_EDGES.t(), String.valueOf(totalEdges));
    }


    //// SAVING & LOADING \\\\

    @Override
    public void readFromNBT(NBTTagCompound compound, boolean array) {
        super.readFromNBT(compound, array);
        fuel = DictionaryEntry.readFromNBTSafely(compound.getCompoundTag("fuel"));
        edgeItem = new ItemStack(compound.getCompoundTag("edgeItem"));
    }

    @Override
    public void writeToNBT(NBTTagCompound compound, boolean array) {
        super.writeToNBT(compound, array);
        compound.setTag("fuel", DictionaryEntry.writeToNBTSafely(new NBTTagCompound(), fuel));
        compound.setTag("edgeItem", edgeItem.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void readHeaderFromBuf(ByteBuf buf){
        super.readHeaderFromBuf(buf);
        if(buf.readBoolean()){
            fuel = GlobalDictionary.getComponentInfoFromID(buf.readInt());
        }
        edgeItem = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void writeHeaderToBuf(ByteBuf buf){
        super.writeHeaderToBuf(buf);
        buf.writeBoolean(fuel != null);
        if(fuel != null){
            buf.writeInt(fuel.globalID);
        }
        ByteBufUtils.writeItemStack(buf, edgeItem);
    }
}