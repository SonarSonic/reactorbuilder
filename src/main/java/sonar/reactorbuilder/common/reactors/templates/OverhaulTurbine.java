package sonar.reactorbuilder.common.reactors.templates;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import sonar.reactorbuilder.common.ReactorBuilderTileEntity;
import sonar.reactorbuilder.common.dictionary.DictionaryEntry;
import sonar.reactorbuilder.common.dictionary.DictionaryEntryType;
import sonar.reactorbuilder.common.dictionary.GlobalDictionary;
import sonar.reactorbuilder.common.reactors.TemplateType;

import javax.annotation.Nullable;
import java.util.Map;

public class OverhaulTurbine extends AbstractTemplate {

    public static DictionaryEntry casingSolid = GlobalDictionary.getComponentInfo("turbine_casing");
    public static DictionaryEntry casingGlass = GlobalDictionary.getComponentInfo("turbine_glass");

    ////saved info
    public int bearingDiameter;

    ////additional info - calculated by client/server seperately.
    public int totalCoils;
    public int totalShafts;
    public int totalBlades;

    public OverhaulTurbine() {
        super();
    }

    public OverhaulTurbine(String fileName, int diameter, int length, int bearingDiameter) {
        super(fileName, diameter, diameter, length+2);
        this.bearingDiameter = bearingDiameter;
    }


    //// GENERAL \\\\

    @Override
    public TemplateType getTemplateType() {
        return TemplateType.OVERHAUL_TURBINE;
    }

    @Nullable
    @Override
    public DictionaryEntry getComponent(int x, int y, int z) {
        if(isComponent(x, y, z)){
            return blocks[x][y][z];
        }else if(isCasing(x, y, z)){
            return isCasingGlass(x, y, z) ? casingGlass : casingSolid;
        }
        return null;
    }

    public void setCoilExact(DictionaryEntry entry, int x, int y, int z){
        if(z == 1){
            z = zSize-1;
        }
        setComponentInfo(entry, x, y, z);
    }

    public void setBladeExact(DictionaryEntry entry, int z){
        int minD = xSize/2-bearingDiameter/2;
        int maxD = xSize-minD-1;
        for(int X = 0; X<xSize; X++){
            for(int Y = 0; Y<ySize; Y++){
                if(X<minD||Y<minD||X>maxD||Y>maxD){
                    if(X<minD&&Y<minD||X<minD&&Y>maxD||X>maxD&&Y<minD||X>maxD&&Y>maxD)continue;
                    setComponentInfo(entry, X, Y, z);
                }
            }
        }
    }

    @Override
    public boolean isCasing(int x, int y, int z){
        return !isCoil(x, y, z) && !((-1 < x && x < xSize) && (-1 < y && y < ySize) && (-1 < z && z < zSize)) && (-1 < z && z < zSize);
    }

    public boolean isBlade(int x, int y, int z){
        if(!isComponent(x, y, z)){
            return false;
        }
        return z != 0 && z != zSize - 1 && !isShaft(x, y, z);
    }

    public boolean isShaft(int x, int y, int z){
        if(!isComponent(x, y, z)){
            return false;
        }
        DictionaryEntry component = blocks[x][y][z];
        return component != null && component.globalName.equals("rotor_shaft");
    }

    public boolean isCoil(int x, int y, int z){
        if(!isComponent(x, y, z)){
            return false;
        }
        return (z == 0 || z == zSize-1) && x < xSize && y < ySize;
    }


    //// BUILDING \\\\

    @Override
    public int getBuildPasses() {
        return 4;
    }

    @Override
    public String[] getBuildPassNames() {
        return new String[]{"Placing Coils", "Placing Shafts", "Placing Blades", "Placing Casing"};
    }

    @Override
    public int getBuildPassTotal(int buildPass) {
        switch(buildPass){
            case 0:
                return totalCoils;
            case 1:
                return totalShafts;
            case 2:
                return totalBlades;
            case 3:
                return totalGlassCasing + totalSolidCasing;
        }
        return 1;
    }

    @Override
    public boolean canPlaceThisPass(int buildPass, int x, int y, int z, DictionaryEntry info) {
        switch (buildPass){
            case 0:
                return isCoil(x, y, z);
            case 1:
                return isShaft(x, y, z);
            case 2:
                return isBlade(x, y, z);
            case 3:
                return isCasing(x, y, z);
        }
        return info.entryType == DictionaryEntryType.OVERHAUL_COMPONENT;
    }


    //// POSITIONS \\\\

    @Override
    public BlockPos getExtStart(){
        if(casingStart != null){
            return casingStart;
        }
        return casingStart = new BlockPos(-1, -1, 0);
    }

    @Override
    public BlockPos getExtEnd(){
        if(casingEnd != null){
            return casingEnd;
        }
        return casingEnd = new BlockPos(xSize, ySize, zSize -1);
    }

    @Override
    public BlockPos getStartPos(ReactorBuilderTileEntity tileEntity) {
        EnumFacing front = EnumFacing.getFront(tileEntity.getBlockMetadata());
        return super.getStartPos(tileEntity).offset(front.rotateY().getOpposite(), -1);
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
    public void updateAdditionalInfo() {
        super.updateAdditionalInfo();
        totalCoils = 0;
        totalShafts = 0;
        totalBlades = 0;
        totalGlassCasing = 0;
        totalSolidCasing = 0;
        for(int x = -1; x <= xSize; x ++){
            for(int y = -1; y <= ySize; y ++){
                for(int z = -1; z <= zSize; z ++){
                    if(isComponent(x, y, z)){
                        DictionaryEntry componentInfo = blocks[x][y][z];
                        if(componentInfo != null){
                            if(isCoil(x, y, z)){
                                totalCoils++;
                            }else if(isShaft(x, y, z)){
                                totalShafts++;
                            }else if(isBlade(x, y, z)){
                                totalBlades++;
                            }
                        }
                    }else if(isCasing(x, y, z)){
                        if(isCasingGlass(x, y, z)){
                            totalGlassCasing++;
                        }else{
                            totalSolidCasing++;
                        }
                    }
                }
            }
        }
        if(totalSolidCasing != 0)
            required.put(casingSolid, totalSolidCasing);
        if(totalGlassCasing != 0)
            required.put(casingGlass, totalGlassCasing);
    }

    @Override
    public void getStats(Map<String, String> statsMap) {
        statsMap.put("File Name", fileName);
        statsMap.put("Reactor Type", getTemplateType().fileType);
        statsMap.put("Dimensions", xSize + " x " + ySize + " x "  + zSize);

        statsMap.put("Components", String.valueOf(totalSolidComponents));
        statsMap.put("Casing", String.valueOf(totalSolidCasing + totalGlassCasing));
    }


    //// SAVING & LOADING \\\\

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        bearingDiameter = compound.getInteger("diameter");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("diameter", bearingDiameter);
    }

    @Override
    public void readFromBuf(ByteBuf buf) {
        super.readFromBuf(buf);
        bearingDiameter = buf.readInt();
    }

    @Override
    public void writeToBuf(ByteBuf buf) {
        super.writeToBuf(buf);
        buf.writeInt(bearingDiameter);
    }

}