package sonar.reactorbuilder.registry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class RBTab extends CreativeTabs {

    public static RBTab INSTANCE = new RBTab();

    public RBTab() {
        super("reactorbuilder.tab");
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(RBBlocks.reactorBuilder);
    }
}
