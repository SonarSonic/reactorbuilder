package sonar.reactorbuilder.common.reactors.templates;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import sonar.reactorbuilder.ReactorBuilder;
import sonar.reactorbuilder.common.ReactorBuilderTileEntity;
import sonar.reactorbuilder.common.dictionary.DictionaryEntry;
import sonar.reactorbuilder.common.dictionary.GlobalDictionary;
import sonar.reactorbuilder.common.reactors.EnumCasingConfig;
import sonar.reactorbuilder.common.reactors.TemplateType;
import sonar.reactorbuilder.util.Util;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractTemplate {

    ////saved info
    public String fileName;
    public int xSize, ySize, zSize;
    public DictionaryEntry[][][] blocks;
    public boolean[] caseConfig = new boolean[7];

    ////additional info - calculated by client/server seperately.
    public Map<DictionaryEntry, Integer> required = new HashMap<>();
    public int totalSolidComponents;
    public int totalAirComponents;
    public int totalSolidCasing;
    public int totalGlassCasing;
    public int totalEdges;

    ////client render cache
    public Object bufferState3D;
    public Object bufferState2D;
    public List<Integer> highlights = new ArrayList<>();

    public AbstractTemplate(){}

    public AbstractTemplate(String fileName, int xSize, int ySize, int zSize) {
        this.fileName = fileName;
        this.xSize = xSize;
        this.ySize = ySize;
        this.zSize = zSize;
        this.blocks = new DictionaryEntry[xSize][ySize][zSize];
    }


    //// GENERAL \\\\

    public abstract TemplateType getTemplateType();

    @Nullable
    public abstract DictionaryEntry getComponent(int x, int y, int z);

    public void setComponentInfo(DictionaryEntry info, int xPos, int yPos, int zPos){
        blocks[xPos][yPos][zPos] = info;
    }

    public boolean isComponent(int x, int y, int z){
        return (getIntStart().getX() <= x && x <= getIntEnd().getX()) && (getIntStart().getY() <= y && y <= getIntEnd().getY()) && (getIntStart().getZ() <= z && z <= getIntEnd().getZ());
    }

    public boolean isCasing(int x, int y, int z){
        return !isComponent(x, y, z) && !isEdge(x, y, z);
    }

    public boolean isEdge(int x, int y, int z){
        boolean xEdge = x == getExtStart().getX() || x == getExtEnd().getX();
        boolean yEdge = y == getExtStart().getY() || y == getExtEnd().getY();
        boolean zEdge = z == getExtStart().getZ() || z == getExtEnd().getZ();
        return (xEdge && yEdge) || (xEdge && zEdge) || (yEdge && zEdge);
    }

    public boolean isCasingGlass(int x, int y, int z){
        if(isEdge(x, y, z)){
            return caseConfig[6];
        }

        if(isCasing(x, y, z)){
            EnumFacing face = null;
            if(x == getExtStart().getX()){
                face = EnumFacing.WEST;
            } else if(x == getExtEnd().getX()){
                face = EnumFacing.EAST;
            } else if(y == getExtStart().getY()){
                face = EnumFacing.DOWN;
            } else if(y == getExtEnd().getY()){
                face = EnumFacing.UP;
            } else if(z == getExtStart().getZ()){
                face = EnumFacing.NORTH;
            } else if(z == getExtEnd().getZ()){
                face = EnumFacing.SOUTH;
            }
            return face != null && caseConfig[face.ordinal()];
        }
        return false;
    }


    //// BUILDING \\\\

    public abstract int getBuildPasses();

    public abstract String[] getBuildPassNames();

    public abstract int getBuildPassTotal(int buildPass);

    public abstract boolean canPlaceThisPass(int buildPass, int x, int y, int z, DictionaryEntry info);


    //// CUSTOM PASS \\\\

    public boolean isCustomPass(int buildPass){
        return false;
    }

    public void tickCustomPass(ReactorBuilderTileEntity builder, int buildPass){}


    //// INFO \\\\

    public abstract DictionaryEntry getDefaultSolidCasing();

    public abstract DictionaryEntry getDefaultGlassCasing();

    public abstract void getStats(Map<String, String> statsMap);

    public void updateAdditionalInfo(){
        required = new HashMap<>();
        totalSolidComponents = 0;
        totalAirComponents = 0;
        totalSolidCasing = 0;
        totalGlassCasing = 0;
        totalEdges = 0;
        for(int x = getExtStart().getX(); x <= getExtEnd().getX(); x ++){
            for(int y = getExtStart().getY(); y <= getExtEnd().getY(); y ++){
                for(int z = getExtStart().getZ(); z <= getExtEnd().getZ(); z ++){
                    if(isComponent(x, y, z)){
                        DictionaryEntry componentInfo = blocks[x][y][z];
                        if(componentInfo != null){
                            Integer total = required.get(componentInfo);
                            required.put(componentInfo, (total == null ? 0 : total) + 1);
                            totalSolidComponents++;
                        }else{
                            totalAirComponents++;
                        }
                    }else if(isCasing(x, y, z)){
                        if(isCasingGlass(x, y, z)){
                            totalGlassCasing++;
                        }else{
                            totalSolidCasing++;
                        }
                    }else if(isEdge(x, y, z)){
                        totalEdges++;
                    }
                }
            }
        }

    }

    public void sortAdditionalInfo(){
        //sorts the required components in descending order.
        required = required.entrySet().stream()
                .sorted((c1, c2) -> -c1.getValue().compareTo(c2.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

    }


    //// POSITIONS \\\\

    protected BlockPos internalStart, internalEnd;
    protected BlockPos casingStart, casingEnd;

    public BlockPos getIntStart(){
        if(internalStart != null){
            return internalStart;
        }
        return internalStart = new BlockPos(0, 0, 0);
    }

    public BlockPos getIntEnd(){
        if(internalEnd != null){
            return internalEnd;
        }
        return internalEnd = new BlockPos(xSize-1, ySize-1, zSize-1);
    }

    public BlockPos getExtStart(){
        if(casingStart != null){
            return casingStart;
        }
        return casingStart = new BlockPos(-1, -1, -1);
    }

    public BlockPos getExtEnd(){
        if(casingEnd != null){
            return casingEnd;
        }
        return casingEnd = new BlockPos(xSize, ySize, zSize);
    }

    public BlockPos getStartPos(ReactorBuilderTileEntity tileEntity){
        EnumFacing front = EnumFacing.getFront(tileEntity.getBlockMetadata());
        BlockPos startPos = tileEntity.getPos().offset(front.getOpposite(), 2).offset(EnumFacing.UP, 1).offset(front.rotateY().getOpposite(), 2);
        switch (front){
            case NORTH:
                startPos = startPos.add(1-xSize, 0, 0);
                break;
            case SOUTH:
                startPos = startPos.add(0, 0, 1-zSize);
                break;
            case WEST:
                startPos = startPos.add(0, 0, 0);
                break;
            case EAST:
                startPos = startPos.add(1-xSize, 0, 1-zSize);
                break;
        }
        return startPos;
    }

    public int getIndexFromInternalPos(int xPos, int yPos, int zPos){
        return (xPos * ySize * zSize) + (yPos * zSize) + zPos;
    }

    public BlockPos getPosFromIndexLoop(int index){
        int i = 0;
        for(int x = 0; x < xSize; x++){
            for(int y = 0; y < ySize; y++){
                for(int z = 0; z < zSize; z++){
                    if(i == index){
                        return new BlockPos(x,y,z);
                    }
                    i++;
                }
            }
        }
        return null;
    }

    public BlockPos getPosFromIndexCalc(int index){
        int xPos = index / (ySize * zSize);
        int yPos = (index -= (xPos * ySize * zSize)) / zSize;
        int zPos = index - yPos * zSize;
        return new BlockPos(xPos, yPos, zPos);
    }


    //// SAVING & LOADING \\\\

    @Nullable
    public static AbstractTemplate readTemplateFromNBT(NBTTagCompound compound){
        if(compound.hasKey("template")){
            NBTTagCompound templateTag = compound.getCompoundTag("template");
            TemplateType type = TemplateType.values()[templateTag.getByte("type")];
            if(type.overhaul == ReactorBuilder.isOverhaul()){
                AbstractTemplate template = type.creator.create();
                template.readFromNBT(templateTag);
                template.updateAdditionalInfo();
                template.sortAdditionalInfo();
                return template;
            }
            return null;
        }
        return null;
    }

    public static void writeTemplateToNBT(NBTTagCompound compound, @Nullable AbstractTemplate template){
        if(template != null){
            NBTTagCompound templateTag = new NBTTagCompound();
            templateTag.setByte("type", (byte)template.getTemplateType().ordinal());
            template.writeToNBT(templateTag);
            compound.setTag("template", templateTag);
        }
    }

    public void readFromNBT(NBTTagCompound compound) {
        ///general data
        fileName = compound.getString("fileName");
        xSize = compound.getInteger("xSize");
        ySize = compound.getInteger("ySize");
        zSize = compound.getInteger("zSize");
        blocks = new DictionaryEntry[xSize][ySize][zSize];
        caseConfig = Util.getBooleanArrayFromByteArray(compound.getByteArray("caseConfig"), 7);

        ///reference list
        Map<Integer, DictionaryEntry> refs = new HashMap<>();
        NBTTagList list = compound.getTagList("refs", Constants.NBT.TAG_COMPOUND);
        for(int i = 0; i < list.tagCount(); i++){
            NBTTagCompound componentTag = list.getCompoundTagAt(i);
            DictionaryEntry info = DictionaryEntry.readFromNBTSafely(componentTag);
            int oldID = componentTag.getInteger("oldID");
            refs.put(oldID, info);
        }

        ///block array
        int[] blockArray = compound.getIntArray("blocks");
        int count = 0;
        for(int x = 0; x < xSize; x ++){
            for(int y = 0; y < ySize; y ++){
                for(int z = 0; z < zSize; z ++){
                    blocks[x][y][z] = refs.get(blockArray[count]);
                    count++;
                }
            }
        }
    }


    public void writeToNBT(NBTTagCompound compound) {
        ///general data
        compound.setString("fileName", fileName);
        compound.setInteger("xSize", xSize);
        compound.setInteger("ySize", ySize);
        compound.setInteger("zSize", zSize);
        compound.setByteArray("caseConfig", Util.getByteArrayFromBooleanArray(caseConfig, 7));

        ///reference list
        if(required.isEmpty()){
            updateAdditionalInfo();
            sortAdditionalInfo();
        }

        NBTTagList list = new NBTTagList();

        for(DictionaryEntry info  : required.keySet()){
            NBTTagCompound componentTag = new NBTTagCompound();
            DictionaryEntry.writeToNBTSafely(componentTag, info);
            componentTag.setInteger("oldID", info.globalID);
            list.appendTag(componentTag);
        }
        compound.setTag("refs", list);

        ///block array
        int[] blockArray = new int[xSize * ySize * zSize];

        int count = 0;
        for(int x = 0; x < xSize; x ++){
            for(int y = 0; y < ySize; y ++){
                for(int z = 0; z < zSize; z ++){
                    DictionaryEntry componentInfo = blocks[x][y][z];
                    blockArray[count] = componentInfo == null ? -1 : componentInfo.globalID;
                    count++;
                }
            }
        }
        compound.setIntArray("blocks", blockArray);

    }

    //// BUF - SAVE + LOAD \\\\

    @Nullable
    public static AbstractTemplate readTemplateFromByteBuf(ByteBuf buf){
        if(buf.readBoolean()){
            TemplateType type = TemplateType.values()[buf.readByte()];
            AbstractTemplate template = type.creator.create();
            template.readFromBuf(buf);
            template.updateAdditionalInfo();
            template.sortAdditionalInfo();
            return template;
        }
        return null;
    }

    @Nullable
    public static void writeTemplateToByteBuf(ByteBuf buf, AbstractTemplate template){
        buf.writeBoolean(template != null);
        if(template != null){
            buf.writeByte((byte)template.getTemplateType().ordinal());
            template.writeToBuf(buf);
        }
    }

    public void readFromBuf(ByteBuf buf){
        fileName = ByteBufUtils.readUTF8String(buf);
        xSize = buf.readInt();
        ySize = buf.readInt();
        zSize = buf.readInt();

        for(int i = 0; i < EnumCasingConfig.values().length; i++){
            caseConfig[i] = buf.readBoolean();
        }

        blocks = new DictionaryEntry[xSize][ySize][zSize];

        for(int x = 0; x < xSize; x ++){
            for(int y = 0; y < ySize; y ++){
                for(int z = 0; z < zSize; z ++){
                    blocks[x][y][z] = GlobalDictionary.getComponentInfoFromID(buf.readShort());
                }
            }
        }


    }

    public void writeToBuf(ByteBuf buf){
        ByteBufUtils.writeUTF8String(buf, fileName);
        buf.writeInt(xSize);
        buf.writeInt(ySize);
        buf.writeInt(zSize);


        for(int i = 0; i < EnumCasingConfig.values().length; i++){
            buf.writeBoolean(caseConfig[i]);
        }

        for(int x = 0; x < xSize; x ++){
            for(int y = 0; y < ySize; y ++){
                for(int z = 0; z < zSize; z ++){
                    DictionaryEntry componentInfo = blocks[x][y][z];
                    buf.writeShort(componentInfo == null ? -1 : componentInfo.globalID);
                }
            }
        }
    }
}
