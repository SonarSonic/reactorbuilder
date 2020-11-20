package sonar.reactorbuilder.common;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import sonar.reactorbuilder.common.ReactorBuilderTileEntity;
import sonar.reactorbuilder.common.dictionary.DictionaryEntry;

public class ReactorBuilderRenderer extends TileEntitySpecialRenderer<ReactorBuilderTileEntity> {

    public static AxisAlignedBB FULL_BLOCK = new AxisAlignedBB(0, 0, 0, 1, 1, 1);

    @Override
    public void render(ReactorBuilderTileEntity te, double x, double y, double z, float partialTicks, int destroyStage, float alpha){
        if(te == null){
            return;
        }

        if(te.template != null){
            BlockPos startPos = te.getStartPos();

            ///setup highlight rendering
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0, 240);

            ///render internal size
            AxisAlignedBB bb = new AxisAlignedBB(te.template.getIntStart(), te.template.getIntEnd().add(1,1,1));
            drawBoundingBox(bb, startPos, partialTicks, 0F, 0F, 0F, 1F);

            ///render casing size
            AxisAlignedBB casing = new AxisAlignedBB(te.template.getExtStart(), te.template.getExtEnd().add(1,1,1));
            drawBoundingBox(casing, startPos, partialTicks, 0F, 0F, 0F, 1F);

            ///render error position
            if(te.errorPosition != null){
                GlStateManager.disableDepth();
                drawBoundingBox(FULL_BLOCK, te.errorPosition, partialTicks, 1F, 0.1F, 0.1F, 1F);
                GlStateManager.enableDepth();
            }

            ///render block highlights
            if(!te.template.highlights.isEmpty()){
                GlStateManager.disableDepth();
                for(int xPos = te.template.getExtStart().getX(); xPos <= te.template.getExtEnd().getX(); xPos ++){
                    for(int yPos = te.template.getExtStart().getY(); yPos <= te.template.getExtEnd().getY(); yPos ++){
                        for(int zPos = te.template.getExtStart().getZ(); zPos <= te.template.getExtEnd().getZ(); zPos ++){
                            DictionaryEntry component = te.template.getComponent(xPos, yPos, zPos);
                            if(component != null && te.template.highlights.contains(component.globalID)){
                                drawBoundingBox(FULL_BLOCK, startPos.add(xPos, yPos, zPos), partialTicks, 0.1F, 0.4F, 1F, 1F);
                            }
                        }
                    }
                }
                GlStateManager.enableDepth();
            }

            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
            ///end highlight rendering

        }
    }

    /// from RenderGlobal...

    public static void drawBoundingBox(AxisAlignedBB box, BlockPos pos, float partialTicks, float r, float g, float b, float alpha) {

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(3.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        if (Minecraft.getMinecraft().world.getWorldBorder().contains(pos)) {
            double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)partialTicks;
            double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)partialTicks;
            double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)partialTicks;
            GlStateManager.translate(-d0 + pos.getX(), -d1 + pos.getY(), -d2 + pos.getZ());
            RenderGlobal.drawSelectionBoundingBox(box.expand(-0.002D, -0.002D, -0.002D), r, g, b, alpha);
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

    }

    @Override
    public boolean isGlobalRenderer(ReactorBuilderTileEntity te){
        return true;
    }
}
