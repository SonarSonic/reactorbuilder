package sonar.reactorbuilder.common;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import sonar.reactorbuilder.common.dictionary.DictionaryEntry;
import sonar.reactorbuilder.common.reactors.templates.AbstractTemplate;
import sonar.reactorbuilder.common.reactors.EnumCasingConfig;
import sonar.reactorbuilder.common.reactors.templates.UnderhaulSFRTemplate;
import sonar.reactorbuilder.client.renderer.TemplateRenderer;
import sonar.reactorbuilder.network.EnumSyncPacket;
import sonar.reactorbuilder.network.PacketHandler;
import sonar.reactorbuilder.network.PacketTileSync;
import sonar.reactorbuilder.util.Translate;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class ReactorBuilderGuiPages extends Gui {

    public enum EnumPages{
        REACTOR_STATS(new ReactorBuilderGuiPages.StatsPage()),
        COMPONENTS(new ReactorBuilderGuiPages.ComponentsPage()),
        RENDER_REACTOR(new ReactorBuilderGuiPages.ReactorPage());

        public ReactorBuilderGuiPages page;

        EnumPages(ReactorBuilderGuiPages page) {
            this.page = page;
        }
    }

    public abstract void renderPage(ReactorBuilderGui gui, float relativeX, float relativeY);

    public abstract int getPageSize(ReactorBuilderGui gui);

    public void onClicked(ReactorBuilderGui gui, float relativeX, float relativeY){

    }

    public static class StatsPage extends ReactorBuilderGuiPages {

        @Override
        public void renderPage(ReactorBuilderGui gui, float relativeX, float relativeY) {
            GlStateManager.scale(0.75, 0.75, 0.75);
            AbstractTemplate template = gui.builder.template;

            int totalY = -10;

            drawString(gui.getFont(), TextFormatting.BLUE + Translate.STATUS.t() + ": " + TextFormatting.RESET + gui.getBuilderStatus(), 4, totalY+=12, -1);

            if(template != null){
                Map<String, String> stats = new LinkedHashMap<>();
                template.getStats(stats);

                for(Map.Entry<String,String> stat : stats.entrySet()){
                    if(!(stat.getValue().isEmpty() && stat.getKey().isEmpty())){
                        drawString(gui.getFont(), TextFormatting.BLUE +  stat.getKey() + ": " + TextFormatting.RESET + stat.getValue(), 4, totalY+=12, -1);
                    }else{
                        totalY+=5;
                    }
                }
                drawString(gui.getFont(), TextFormatting.BLUE + "Build Pass" + ": " + TextFormatting.RESET + (gui.builder.buildPass+1) + "/"+ template.getBuildPasses(), 4, totalY+=12, -1);
            }
            if(gui.builder.isBuilding){
                int seconds = (int)((gui.builder.getTotalProgress() - gui.builder.getProgress())/gui.builder.getBlocksPerTick())/20;
                int secondsLeft = seconds % 3600 % 60;
                int minutes = (int) Math.floor(seconds % 3600 / 60);
                int hours = (int) Math.floor(seconds / 3600);
                String timeString = (hours != 0 ? hours +  " h " : "") + (minutes != 0 ? minutes +  " m " : "") + (secondsLeft != 0 ? secondsLeft +  " s " : "");
                drawString(gui.getFont(), TextFormatting.BLUE + "Time Remaining" + ": " + TextFormatting.RESET + timeString, 4, totalY+=12, -1);
            }

        }

        @Override
        public int getPageSize(ReactorBuilderGui gui) {
            int totalY = 12;
            if(gui.builder.template != null){
                Map<String, String> stats = new LinkedHashMap<>();
                gui.builder.template.getStats(stats);
                totalY += stats.size()*12;
            }
            return totalY;
        }
    }

    public static class ComponentsPage extends ReactorBuilderGuiPages {

        @Override
        public void onClicked(ReactorBuilderGui gui, float relativeX, float relativeY) {
            super.onClicked(gui, relativeX, relativeY);

            /// case configuration
            if(10 <= relativeY && relativeY < 28){
                int config = (int)(relativeX-6)/21;
                if(config < 7){

                    if(config == 6 && gui.builder.template instanceof UnderhaulSFRTemplate){
                        ItemStack stack = gui.mc.player.inventory.getItemStack().copy();
                        stack.setCount(1);
                        ((UnderhaulSFRTemplate) gui.builder.template).edgeItem = stack;
                        gui.builder.template.caseConfig[config] = !stack.isEmpty();
                    }else{
                        gui.builder.template.caseConfig[config] = !gui.builder.template.caseConfig[config];
                    }

                    gui.builder.template.updateAdditionalInfo();
                    gui.builder.template.sortAdditionalInfo();
                    PacketHandler.INSTANCE.sendToServer(new PacketTileSync(gui.builder, EnumSyncPacket.SYNC_CASING_TYPES));
                }
            }

            ///component highlighting
            float itemStart = (44+14+12)*0.75F;
            float itemHeight = 18*0.75F;
            if(itemStart <= relativeY){
                int itemPos =  (int)((relativeY-itemStart)/itemHeight);

                Map.Entry<DictionaryEntry, Integer> entry = null;
                int pos = 0;
                for(Map.Entry<DictionaryEntry, Integer> required : gui.builder.template.required.entrySet()){
                    if(pos == itemPos){
                        entry = required;
                        break;
                    }
                    pos++;
                }
                if(entry != null){
                    Integer globalID = entry.getKey().globalID;
                    if(gui.builder.template.highlights.contains(globalID)){
                        gui.builder.template.highlights.removeIf(i -> i.equals(globalID));
                    }else{
                        gui.builder.template.highlights.add(globalID);
                    }
                }

                System.out.println(entry != null ? entry.getKey().globalName : "null");
            }

        }

        @Override
        public void renderPage(ReactorBuilderGui gui, float relativeX, float relativeY) {
            if(gui.builder.template != null){

                /// case configuration
                RenderHelper.enableGUIStandardItemLighting();
                ItemStack solid = gui.builder.template.getDefaultSolidCasing().getItemStack();
                ItemStack glass = gui.builder.template.getDefaultGlassCasing().getItemStack();

                for(int i = 0; i < 7; i++){
                    ItemStack stack = gui.builder.template.caseConfig[i] ? glass : solid;
                    if(i == 6 && gui.builder.template instanceof UnderhaulSFRTemplate){
                        stack = ((UnderhaulSFRTemplate) gui.builder.template).edgeItem;
                    }
                    gui.getRenderItem().renderItemIntoGUI(stack, 6 + 21*i, 14);
                }

                GlStateManager.scale(0.50, 0.50, 0.50);
                for(int i = 0; i < 7; i++){
                    gui.drawCenteredString(gui.getFont(), EnumCasingConfig.values()[i].name().toLowerCase(), (int)((5.5 + (21*i)+9)/0.5), (int)((32)/0.5), -1);
                }
                GlStateManager.scale(1/0.50, 1/0.50, 1/0.50);


                ///component highlighting
                GlStateManager.scale(0.75, 0.75, 0.75);
                drawString(gui.getFont(), TextFormatting.BLUE + Translate.CASING_CONFIG.t() + ": ", 4, 4, -1);
                int totalY = 44;
                drawString(gui.getFont(), TextFormatting.BLUE + Translate.REQUIRED_COMPONENTS.t() + ": ", 4, totalY+=14, -1);
                totalY+=12;
                for(Map.Entry<DictionaryEntry, Integer> required : gui.builder.template.required.entrySet()){
                    boolean highlighted = gui.builder.template.highlights.contains(required.getKey().globalID);
                    ItemStack stack = required.getKey().getItemStack();
                    gui.getRenderItem().renderItemIntoGUI(stack, 10, totalY);
                    gui.drawString(gui.getFont(),  (highlighted ? "" + TextFormatting.BLUE : "") + required.getValue() + " x " + stack.getDisplayName(), 10 + 24, 4 + totalY, -1);
                    totalY+=18;
                }
                RenderHelper.disableStandardItemLighting();
            }
        }

        @Override
        public int getPageSize(ReactorBuilderGui gui) {
            if(gui.builder.template != null){
                return (int)(((gui.builder.template.required.size()+2) * 18)*0.75) + 40;
            }
            return 0;
        }
    }

    public static class ReactorPage extends ReactorBuilderGuiPages {

        public long startRotate = -1;

        @Override
        public void renderPage(ReactorBuilderGui gui, float relativeX, float relativeY) {
            float rotateTime = 20000; //20 second rotation
            long currentTime = System.currentTimeMillis();
            if(startRotate == -1 || startRotate + rotateTime < currentTime){
                startRotate = System.currentTimeMillis();
            }
            float percentage = currentTime-startRotate;
            float rotation = 360F*(percentage/rotateTime);

            AbstractTemplate template = gui.builder.template;

            if(template == null){
                return;
            }

           // RenderHelper.enableStandardItemLighting();

            GlStateManager.pushMatrix();
            gui.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            gui.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.translate(0, 0, 100.0F + this.zLevel);
            GlStateManager.scale(1.0F, -1.0F, 1.0F);
           // GlStateManager.enableLighting();
            float scaling = Math.min(((float)ReactorBuilderGui.WINDOW_WIDTH) / (template.xSize), ((float)ReactorBuilderGui.WINDOW_HEIGHT) / (template.ySize)) / 2;
            GlStateManager.scale(scaling, scaling, scaling);

            GlStateManager.translate(((float)ReactorBuilderGui.WINDOW_WIDTH/scaling)/2, -((float)ReactorBuilderGui.WINDOW_HEIGHT/scaling)/2, 0.0F);
            GlStateManager.rotate(30, 1, 0, 0);
            GlStateManager.rotate(rotation, 0, 1, 0);
            GlStateManager.translate(- template.xSize/2D, - template.ySize/2D, - template.zSize/2D);

            TemplateRenderer.renderCachedTemplate(template);

            GlStateManager.disableAlpha();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableLighting();
            GlStateManager.popMatrix();
            gui.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            gui.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();



            GlStateManager.pushMatrix();
            GlStateManager.translate(0, ReactorBuilderGui.WINDOW_HEIGHT, 0 );
            GlStateManager.translate(0, 0, 100.0F + this.zLevel); //WINDOW EDGE

            float flatScaling = Math.min(ReactorBuilderGui.WINDOW_WIDTH / template.xSize, ReactorBuilderGui.WINDOW_HEIGHT / (template.zSize)) * 0.8F;
            GlStateManager.scale(flatScaling, flatScaling, flatScaling);

            GlStateManager.translate((ReactorBuilderGui.WINDOW_WIDTH/flatScaling)/2, (ReactorBuilderGui.WINDOW_HEIGHT/flatScaling)/2, 0.0F);
            GlStateManager.translate(-0.5-(template.xSize)/2, -0.5-(template.zSize)/2, 0.0F);

            TemplateRenderer.renderCachedLayers(template, ReactorBuilderGui.WINDOW_HEIGHT/flatScaling);

            //RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();

            for(int i = 0; i < template.ySize; i ++){
                gui.drawCenteredString(gui.getFont(), String.valueOf(i + 1), 30, 8 + 75 + 75/2 - 9/2 + 75*i, -1);
            }
        }

        @Override
        public int getPageSize(ReactorBuilderGui gui) {
            return 75 + (gui.builder.template == null ?  0 : gui.builder.template.ySize*75);
        }
    }

}
