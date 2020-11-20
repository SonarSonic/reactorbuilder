package sonar.reactorbuilder.common.reactors.templates;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import sonar.reactorbuilder.common.ReactorBuilderTileEntity;
import sonar.reactorbuilder.common.dictionary.DictionaryEntry;
import sonar.reactorbuilder.common.dictionary.DictionaryEntryType;
import sonar.reactorbuilder.common.dictionary.GlobalDictionary;
import sonar.reactorbuilder.common.reactors.TemplateType;
import sonar.reactorbuilder.registry.RBConfig;
import sonar.reactorbuilder.util.OverhaulHelper;
import sonar.reactorbuilder.util.Translate;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

///for SFR and MSR
public abstract class OverhaulFissionTemplate extends AbstractTemplate {

    public static DictionaryEntry casingSolid = GlobalDictionary.getComponentInfo("sfr_casing");
    public static DictionaryEntry casingGlass = GlobalDictionary.getComponentInfo("sfr_glass");

    public Map<DictionaryEntry, List<Integer>> recipeToIndexMap = new HashMap<>();

    ///additional info
    public int totalRecipes;
    public List<DictionaryEntry> fuels;
    public List<DictionaryEntry> recipes;
    public String fuelNameList;
    public String recipeNameList;


    public OverhaulFissionTemplate(){}

    public OverhaulFissionTemplate(String fileName, int xSize, int ySize, int zSize) {
        super(fileName, xSize, ySize, zSize);
    }



    //// GENERAL \\\\

    @Nullable
    @Override
    public DictionaryEntry getComponent(int x, int y, int z){
        if(isComponent(x, y, z)){
            return blocks[x][y][z];
        }
        if(isCasing(x, y, z) || isEdge(x, y, z)){
            return isCasingGlass(x, y, z) ? casingGlass : casingSolid;
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
        return new String[]{Translate.PASS_PLACING_COMPONENTS.t(), Translate.PASS_SETTING_FILTERS.t(), Translate.PASS_PLACING_CASINGS.t()};
    }

    @Override
    public int getBuildPassTotal(int buildPass) {
        switch (buildPass){
            case 0: return totalSolidComponents;
            case 1: return totalRecipes;
            case 2: return totalGlassCasing + totalSolidCasing + totalEdges;
        }
        return 0;
    }

    @Override
    public boolean canPlaceThisPass(int buildPass, int x, int y, int z, DictionaryEntry info) {
        switch (buildPass){
            case 0: return info.entryType == DictionaryEntryType.OVERHAUL_COMPONENT;
            case 1: return false;
            case 2: return info.entryType == DictionaryEntryType.OVERHAUL_CASING_SOLID ||info.entryType == DictionaryEntryType.OVERHAUL_CASING_GLASS;
        }
        return false;
    }



    //// CUSTOM PASS \\\\

    @Override
    public boolean isCustomPass(int buildPass) {
        return buildPass == 1;
    }

    @Override
    public void tickCustomPass(ReactorBuilderTileEntity builder, int buildPass) {
        super.tickCustomPass(builder, buildPass);
        if(buildPass == 1){ //set item & fluid filters
            int index = 0;
            for(Map.Entry<DictionaryEntry, List<Integer>> map : recipeToIndexMap.entrySet()){
                DictionaryEntry input = map.getKey();
                if(input.entryType == DictionaryEntryType.OVERHAUL_FUEL || input.entryType == DictionaryEntryType.OVERHAUL_LIQUID_FUEL){
                    if(fuels.size() == 1){
                        index += map.getValue().size();
                        builder.passProgress[buildPass] = index;
                        continue;
                    }
                }
                if(input.entryType == DictionaryEntryType.IRRADIATOR_RECIPE){
                    if(recipes.size() == 1){
                        index += map.getValue().size();
                        builder.passProgress[buildPass] = index;
                        continue;
                    }
                }

                for(Integer i : map.getValue()){
                    index++;
                    if(index > builder.passProgress[buildPass]){
                        builder.passProgress[buildPass]++;
                        BlockPos pos = getPosFromIndexLoop(i);
                        DictionaryEntry info = getComponent(pos.getX(), pos.getY(), pos.getZ());
                        BlockPos nextPos = builder.getStartPos().add(pos);
                        if(info == null || !builder.isMatchingComponentAtPos(info, nextPos)){
                            continue;
                        }
                        if(RBConfig.allowFuelCellFiltering && info.globalName.equals("fuel_cell")){
                            TileEntity tile = builder.getWorld().getTileEntity(nextPos);
                            OverhaulHelper.setItemStackFilter(tile, input.getItemStack());
                        }else if(RBConfig.allowIrradiatorFiltering && info.globalName.equals("neutron_irradiator")){
                            TileEntity tile = builder.getWorld().getTileEntity(nextPos);
                            OverhaulHelper.setItemStackFilter(tile, input.getItemStack());
                        }else if(RBConfig.allowFuelVesselFiltering && info.globalName.equals("fuel_vessel")){
                            TileEntity tile = builder.getWorld().getTileEntity(nextPos);
                            OverhaulHelper.setFluidStackFilter(tile, input.getFluidStack());
                        }
                    }
                }
            }
        }
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
        if(totalSolidCasing + totalEdges != 0)
            required.put(casingSolid, totalSolidCasing + totalEdges);
        if(totalGlassCasing != 0)
            required.put(casingGlass, totalGlassCasing);

        totalRecipes = 0;

        fuels = new ArrayList<>();
        recipes = new ArrayList<>();

        for(Map.Entry<DictionaryEntry, List<Integer>> map : recipeToIndexMap.entrySet()){
            if(map.getKey().entryType == DictionaryEntryType.OVERHAUL_FUEL || map.getKey().entryType == DictionaryEntryType.OVERHAUL_LIQUID_FUEL)
                fuels.add(map.getKey());
            if(map.getKey().entryType == DictionaryEntryType.IRRADIATOR_RECIPE)
                recipes.add(map.getKey());
            totalRecipes+=map.getValue().size();
        }

        fuelNameList = DictionaryEntry.toStringList(fuels);
        recipeNameList = DictionaryEntry.toStringList(recipes);
    }

    @Override
    public void getStats(Map<String, String> statsMap) {
        statsMap.put(Translate.TEMPLATE_FILE_NAME.t(), fileName);
        statsMap.put(Translate.TEMPLATE_REACTOR_TYPE.t(), getTemplateType().fileType);
        statsMap.put(Translate.TEMPLATE_DIMENSIONS.t(), xSize + " x " + ySize + " x "  + zSize);

        statsMap.put(Translate.TEMPLATE_FUEL_TYPES.t(), fuelNameList);
        statsMap.put(Translate.TEMPLATE_IRRADIATOR_FILTERS.t(), recipeNameList);

        statsMap.put(Translate.TEMPLATE_COMPONENTS.t(), String.valueOf(totalSolidComponents));
        statsMap.put(Translate.CASING_CONFIG.t(), String.valueOf(totalSolidCasing + totalGlassCasing + totalEdges));
    }



    //// SAVING & LOADING \\\\

    @Override
    public void readFromNBT(NBTTagCompound compound, boolean array) {
        super.readFromNBT(compound, array);
        if(!array){ //recipe maps can easily become too big for ByteBufs - TODO - WE MUST SEND THIS STUFF!!!
            return;
        }
        recipeToIndexMap.clear();
        NBTTagList fuelMapList = compound.getTagList("recipeMap", Constants.NBT.TAG_COMPOUND);
        for(int i = 0; i < fuelMapList.tagCount(); i ++){
            NBTTagCompound fuelCompound = fuelMapList.getCompoundTagAt(i);
            DictionaryEntry fuelInfo = DictionaryEntry.readFromNBTSafely(fuelCompound);
            int[] indexes = fuelCompound.getIntArray("indexes");
            List<Integer> indexList = Arrays.stream(indexes).boxed().collect(Collectors.toList());
            recipeToIndexMap.putIfAbsent(fuelInfo, indexList);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound, boolean array) {
        super.writeToNBT(compound, array);
        if(!array){
            return;
        }
        NBTTagList fuelMapList = new NBTTagList();
        for(Map.Entry<DictionaryEntry, List<Integer>> entry : recipeToIndexMap.entrySet()){
            NBTTagCompound fuelCompound = new NBTTagCompound();
            DictionaryEntry.writeToNBTSafely(fuelCompound, entry.getKey());
            int[] indexes = entry.getValue().stream().mapToInt(i -> i).toArray();
            fuelCompound.setIntArray("indexes", indexes);
            fuelMapList.appendTag(fuelCompound);
        }
        compound.setTag("recipeMap", fuelMapList);
    }

    @Override
    public void readHeaderFromBuf(ByteBuf buf){
        super.readHeaderFromBuf(buf);
        recipeToIndexMap.clear();
        int mapSize = buf.readInt();
        for(int f = 0; f < mapSize; f++){
            DictionaryEntry fuel = GlobalDictionary.getComponentInfoFromID(buf.readShort());
            List<Integer> indexes = new ArrayList<>();
            int indexSize = buf.readInt();
            for(int i = 0; i < indexSize; i++){
                indexes.add(buf.readInt());
            }
            recipeToIndexMap.putIfAbsent(fuel, indexes);
        }
    }

    @Override
    public void writeHeaderToBuf(ByteBuf buf){
        super.writeHeaderToBuf(buf);
        buf.writeInt(recipeToIndexMap.size());
        for(Map.Entry<DictionaryEntry, List<Integer>> entry : recipeToIndexMap.entrySet()){
            buf.writeShort(entry.getKey().globalID);
            buf.writeInt(entry.getValue().size());
            for(Integer i : entry.getValue()){
                buf.writeInt(i);
            }
        }
    }

    public static class SFR extends OverhaulFissionTemplate{

        public SFR() {
            super();
        }

        public SFR(String fileName, int xSize, int ySize, int zSize) {
            super(fileName, xSize, ySize, zSize);
        }

        @Override
        public TemplateType getTemplateType() {
            return TemplateType.OVERHAUL_SFR;
        }

    }

    public static class MSR extends OverhaulFissionTemplate{

        public MSR() {
            super();
        }

        public MSR(String fileName, int xSize, int ySize, int zSize) {
            super(fileName, xSize, ySize, zSize);
        }

        @Override
        public TemplateType getTemplateType() {
            return TemplateType.OVERHAUL_MSR;
        }
    }

}