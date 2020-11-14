package sonar.reactorbuilder.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.reactorbuilder.common.ReactorBuilderBlock;

public class RBBlocks {

    @GameRegistry.ObjectHolder("reactorbuilder:reactorbuilder")
    public static ReactorBuilderBlock reactorBuilder;
    @GameRegistry.ObjectHolder("reactorbuilder:creativereactorbuilder")
    public static ReactorBuilderBlock creativeReactorBuilder;

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        reactorBuilder.initModel();
        creativeReactorBuilder.initModel();
    }

    @SideOnly(Side.CLIENT)
    public static void initItemModels() {

    }
}
