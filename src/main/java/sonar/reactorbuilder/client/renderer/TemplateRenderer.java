package sonar.reactorbuilder.client.renderer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import sonar.reactorbuilder.common.dictionary.DictionaryEntry;
import sonar.reactorbuilder.common.reactors.templates.AbstractTemplate;

import java.util.List;

public class TemplateRenderer {

    public static void renderCachedTemplate(AbstractTemplate template){
        if(template.bufferState3D != null){
            ///render from cached buffer
            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            buffer.begin(7, DefaultVertexFormats.BLOCK);
            buffer.setVertexState((BufferBuilder.State)template.bufferState3D);
            Tessellator.getInstance().draw();
        }else{
            ///render for the first time
            TemplateBlockAccess blockAccess = new TemplateBlockAccess(template);
            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            BlockRendererDispatcher renderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
            buffer.begin(7, DefaultVertexFormats.BLOCK);
            for(int x = 0; x < template.xSize; x ++){
                for(int y = 0; y < template.ySize; y ++){
                    for(int z = 0; z < template.zSize; z ++){
                        DictionaryEntry info = template.blocks[x][y][z];
                        if(info != null){
                            BlockPos actualPos = new BlockPos(x,y,z);
                            IBlockState state = info.getBlockState();
                            IBakedModel bakedModel = renderer.getModelForState(state);
                            renderer.getBlockModelRenderer().renderModel(blockAccess, bakedModel, state, actualPos, buffer, true);
                        }
                    }
                }
            }
            template.bufferState3D = buffer.getVertexState();
            Tessellator.getInstance().draw();
        }
    }

    public static void renderCachedLayers(AbstractTemplate template, double yOffset){

        if(template.bufferState2D != null){
            ///render from cached buffer
            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            buffer.setVertexState((BufferBuilder.State)template.bufferState2D);
            Tessellator.getInstance().draw();
        }else{
            ///render for the first time
            BlockRendererDispatcher renderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            buffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            for(int y = 0; y < template.ySize; y++){
                for(int x = 0; x < template.xSize; x++){
                    for(int z = 0; z < template.zSize; z++){
                        DictionaryEntry info = template.getComponent(x, y, z);
                        if(info != null){
                            IBlockState state = info.getBlockState();
                            IBakedModel bakedModel = renderer.getModelForState(state);
                            renderBlockFaceRapidly(buffer, bakedModel, state, x, z + yOffset*y, 0, 1, 1);
                        }
                    }
                }
            }
            template.bufferState2D = buffer.getVertexState();
            Tessellator.getInstance().draw();
        }
    }

    public static void renderBlockFaceRapidly(BufferBuilder buffer, IBakedModel modelIn, IBlockState stateIn, double x, double y, double z, double width, double height){
        List<BakedQuad> list = modelIn.getQuads(stateIn, EnumFacing.UP, 0);
        TextureAtlasSprite sprite = list.isEmpty() ? modelIn.getParticleTexture() : list.get(0).getSprite();

        buffer.pos(x + 0, y + height, z).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
        buffer.pos(x + width, y + height, z).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
        buffer.pos(x + width, y + 0, z).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
        buffer.pos(x + 0, y + 0, z).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
    }

}