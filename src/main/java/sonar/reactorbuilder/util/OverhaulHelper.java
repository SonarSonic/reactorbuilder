package sonar.reactorbuilder.util;

import nc.tile.fluid.ITileFilteredFluid;
import nc.tile.inventory.ITileFilteredInventory;
import nc.util.FluidStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.FluidStack;

public class OverhaulHelper {

    public static void setItemStackFilter(TileEntity tile, ItemStack stack){
        if(tile instanceof ITileFilteredInventory){
            ITileFilteredInventory cell = (ITileFilteredInventory) tile;
            if (cell.canModifyFilter(0) && ((ItemStack)cell.getInventoryStacks().get(0)).isEmpty() && !stack.isItemEqual(cell.getFilterStacks().get(0)) && cell.isItemValidForSlotInternal(0, stack)) {
                ItemStack filter = stack.copy();
                filter.setCount(1);
                cell.getFilterStacks().set(0, filter);
                cell.onFilterChanged(0);
            }
        }
    }

    public static void setFluidStackFilter(TileEntity tile, FluidStack fluidStack){
        if(tile instanceof ITileFilteredFluid){
            ITileFilteredFluid vessel = (ITileFilteredFluid) tile;
            if (vessel.canModifyFilter(0) && vessel.getTanks().get(0).isEmpty() && fluidStack != null && !FluidStackHelper.stacksEqual(vessel.getFilterTanks().get(0).getFluid(), fluidStack) && vessel.getTanks().get(0).canFillFluidType(fluidStack)) {
                FluidStack filter = fluidStack.copy();
                filter.amount = 1000;
                vessel.getFilterTanks().get(0).setFluid(filter);
                vessel.onFilterChanged(0);
            }
        }
    }

}
