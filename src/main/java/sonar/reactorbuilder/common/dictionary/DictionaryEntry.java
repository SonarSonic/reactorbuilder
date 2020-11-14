package sonar.reactorbuilder.common.dictionary;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import sonar.reactorbuilder.ReactorBuilder;

import javax.annotation.Nullable;
import java.util.List;

public abstract class DictionaryEntry {

    public int globalID;
    public String globalName;
    public DictionaryEntryType entryType;

    public DictionaryEntry(int id, String globalName, DictionaryEntryType entryType) {
        this.globalID = id;
        this.globalName = globalName;
        this.entryType = entryType;
    }

    public abstract String getDisplayName();

    public ItemStack getItemStack(){
        return ((DictionaryEntry.ItemEntry)this).getDefaultItemStack();
    }

    public IBlockState getBlockState(){
        return ((DictionaryEntry.ItemEntry)this).getDefaultBlockState();
    }

    public FluidStack getFluidStack(){
        return ((DictionaryEntry.FluidEntry)this).getDefaultFluidStack();
    }

    @Override
    public int hashCode() {
        return globalID;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof DictionaryEntry){
            DictionaryEntry info = (DictionaryEntry) obj;
            return info.globalID == globalID && info.entryType == entryType && info.globalName.equals(globalName);
        }
        return super.equals(obj);
    }

    ///to write safely, we only need to write the refString and itemStack, as these should persist across world saves.
    @Nullable
    public static DictionaryEntry readFromNBTSafely(NBTTagCompound compound) {
        if(compound.hasKey("type")){
            DictionaryEntryType type = DictionaryEntryType.getType(compound.getByte("type"));
            if(type.isOverhaul == ReactorBuilder.isOverhaul()){
                String globalName = compound.getString("gName");
                DictionaryEntry info = GlobalDictionary.getComponentInfo(globalName);
                if(info != null){
                    return info;
                }
                return null;
            }
        }
        return null;
    }

    public static NBTTagCompound writeToNBTSafely(NBTTagCompound compound, @Nullable DictionaryEntry component){
        if(component != null){
            compound.setString("gName", component.globalName);
            compound.setByte("type", component.entryType.getID());
        }
        return compound;
    }

    public static DictionaryEntry.ItemEntry makeEdgeComponent(ItemStack itemStack){
        return new DictionaryEntry.ItemEntry(-1, "edge", DictionaryEntryType.UNDERHAUL_EDGES, Lists.newArrayList(itemStack.copy()));
    }

    public static String toStringList(List<DictionaryEntry> entries){
        StringBuilder builder  = new StringBuilder();
        for(DictionaryEntry entry : entries){
            if(builder.length() > 0){
                builder.append(", ");
            }
            builder.append(entry.getDisplayName());
        }
        return builder.toString();
    }

    public static class ItemEntry extends DictionaryEntry{

        public List<ItemStack> validStacks;

        public ItemEntry(int id, String globalName, DictionaryEntryType entryType, List<ItemStack> itemStack) {
            super(id, globalName, entryType);
            this.validStacks = itemStack;
        }

        public ItemStack getDefaultItemStack(){
            return validStacks.get(0);
        }

        public IBlockState getDefaultBlockState(){
            Block block = Block.getBlockFromItem(getDefaultItemStack().getItem());
            int metadata = getDefaultItemStack().getMetadata();

            if(entryType == DictionaryEntryType.OVERHAUL_TURBINE_BLADE){
                metadata = 1; //turbine blades are invisible with a metadata of 0
            }

            return block.getStateFromMeta(metadata);
        }

        @Override
        public String getDisplayName() {
            return getDefaultItemStack().getDisplayName();
        }
    }

    public static class FluidEntry extends DictionaryEntry{

        public FluidStack fluidStack;

        public FluidEntry(int id, String globalName, DictionaryEntryType entryType, FluidStack fluidStack) {
            super(id, globalName, entryType);
            this.fluidStack = fluidStack;
        }

        public FluidStack getDefaultFluidStack(){
            return fluidStack;
        }

        @Override
        public String getDisplayName() {
            return fluidStack.getLocalizedName();
        }
    }

}
