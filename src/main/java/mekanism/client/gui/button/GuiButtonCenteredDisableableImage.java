package mekanism.client.gui.button;

import mekanism.client.render.MekanismRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * From GuiButtonImage with a couple fixes and support for rendering differently when disabled
 */
@SideOnly(Side.CLIENT)
public class GuiButtonCenteredDisableableImage extends GuiButton {

    private final ResourceLocation resourceLocation;
    private final int xTexStart;
    private final int yTexStart;
    private final int hoverOffset;
    private final int disabledOffset;

    public String displayString;

    public GuiButtonCenteredDisableableImage(int id, int x, int y, int width, int height, int offsetX, int offsetY, int hoverOffset, ResourceLocation resource, String buttonText) {
        this(id, x, y, width, height, offsetX, offsetY, hoverOffset, 0, resource, buttonText);
    }

    public GuiButtonCenteredDisableableImage(int id, int x, int y, int width, int height, int offsetX, int offsetY, int hoverOffset, int disabledOffset, ResourceLocation resource, String buttonText) {
        super(id, x, y, width, height, buttonText);
        this.xTexStart = offsetX;
        this.yTexStart = offsetY;
        this.hoverOffset = hoverOffset;
        this.disabledOffset = disabledOffset;
        this.resourceLocation = resource;
        this.displayString = buttonText;
    }

    @Override
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            //Ensure the color gets reset. The default GuiButtonImage doesn't so other GuiButton's can have the color leak out of them
            FontRenderer fontrenderer = mc.fontRenderer;
            MekanismRenderer.resetColor();
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            mc.getTextureManager().bindTexture(this.resourceLocation);
            GlStateManager.disableDepth();
            int j = this.yTexStart;

            if (!this.enabled) {
                //Add support for having a different texture for when it is disabled
                j += this.disabledOffset;
            } else if (this.hovered) {
                j += this.hoverOffset;
            }

            this.drawTexturedModalRect(this.x, this.y, this.xTexStart, j, this.width, this.height);
            GlStateManager.enableDepth();

            if (this.enabled) {
                j = 0xffffff;
            } else {
                j = 0x9e9e9e;
            }
            this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
        }
    }
}