package sonar.reactorbuilder.client.renderer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import sonar.reactorbuilder.common.dictionary.DictionaryEntry;
import sonar.reactorbuilder.common.reactors.templates.AbstractTemplate;

import javax.annotation.Nullable;

//used when caching the rendering of templates.
public class TemplateBlockAccess implements IBlockAccess {

    public AbstractTemplate template;

    public TemplateBlockAccess(AbstractTemplate template){
        this.template = template;
    }

    @Override
    public int getCombinedLight(BlockPos pos, int lightValue) {
        return 15;
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        if(!template.isComponent(pos.getX(), pos.getY(), pos.getZ())){ // prevents casing disabling component rendering
            return Blocks.AIR.getDefaultState();
        }
        DictionaryEntry info = template.getComponent(pos.getX(), pos.getY(), pos.getZ());
        return info == null ? Blocks.AIR.getDefaultState() : info.getBlockState();
    }

    @Override
    public boolean isAirBlock(BlockPos pos) {
        if(!template.isComponent(pos.getX(), pos.getY(), pos.getZ())){ // prevents casing disabling component rendering
            return true;
        }
        DictionaryEntry info = template.getComponent(pos.getX(), pos.getY(), pos.getZ());
        return info == null;
    }

    @Override
    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
        return !isAirBlock(pos); //assume most components have solid sides
    }


    //// NOT NEEDED \\\\

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos) {
        return null; //not needed
    }


    @Override
    public Biome getBiome(BlockPos pos) {
        return Biomes.DEFAULT; //not needed
    }

    @Override
    public int getStrongPower(BlockPos pos, EnumFacing direction) {
        return 0; //not needed
    }

    @Override
    public WorldType getWorldType() {
        return WorldType.DEFAULT; //not needed
    }
}
