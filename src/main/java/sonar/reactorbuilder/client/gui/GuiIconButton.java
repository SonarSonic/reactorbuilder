package sonar.reactorbuilder.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;
import sonar.reactorbuilder.common.ReactorBuilderGui;

import javax.annotation.Nonnull;

public class GuiIconButton extends GuiButton {
    protected GuiScreen host;
    protected int textureX;
    protected int textureY;
    protected int sizeX, sizeY;

    public GuiIconButton(GuiScreen host, int id, int x, int y, int textureX, int textureY, int sizeX, int sizeY, String hoverText) {
        super(id, x, y, sizeX, sizeY, hoverText);
        this.host = host;
        this.textureX = textureX;
        this.textureY = textureY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    @Override
    public void drawButton(@Nonnull Minecraft mc, int x, int y, float partialTicks) {
        if (this.visible) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            hovered = x >= this.x && y >= this.y && x < this.x + this.width && y < this.y + this.height;
            mc.getTextureManager().bindTexture(ReactorBuilderGui.buttons);
            drawTexturedModalRect(this.x, this.y, this.textureX, hovered ? this.textureY + 18 : this.textureY, sizeX, sizeY);
        }
    }

    @Override
    public void drawButtonForegroundLayer(int mouseX, int mouseY) {
        super.drawButtonForegroundLayer(mouseX, mouseY);
        if(hovered){
            host.drawHoveringText(displayString, mouseX, mouseY);
        }
    }
}