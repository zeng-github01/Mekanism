package mekanism.client.gui.button;

import mekanism.client.render.MekanismRenderer;
import mekanism.common.util.MekanismUtils;
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
public class GuiButtonTextDisableableImage extends GuiButton {

    private final ResourceLocation Button = MekanismUtils.getResource(MekanismUtils.ResourceType.GUI_ELEMENT, "GuiButton.png");

    public String displayString;

    public GuiButtonTextDisableableImage(int id, int x, int y, int width, int height, String buttonText) {
        super(id, x, y, width, height, buttonText);
        displayString = buttonText;
    }

    @Override
    protected int getHoverState(boolean hoveredOrFocused) {
        if (!enabled) {
            return 0;
        } else if (hoveredOrFocused) {
            return 2;
        }
        return 1;
    }

    @Override
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            //Ensure the color gets reset. The default GuiButtonImage doesn't so other GuiButton's can have the color leak out of them
            FontRenderer fontrenderer = mc.fontRenderer;
            MekanismRenderer.resetColor();
            hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
            int i = getHoverState(hovered);
            int position = i * 20;
            int halfWidthLeft = width / 2;
            int halfWidthRight = width % 2 == 0 ? halfWidthLeft : halfWidthLeft + 1;
            int halfHeightTop = height / 2;
            int halfHeightBottom = height % 2 == 0 ? halfHeightTop : halfHeightTop + 1;
            mc.getTextureManager().bindTexture(Button);
            GlStateManager.disableDepth();
            drawTexturedModalRect(x, y, 0, position, halfWidthLeft, halfHeightTop);
            drawTexturedModalRect(x, y + halfHeightTop, 0, position + 20 - halfHeightBottom, halfWidthLeft, halfHeightBottom);
            drawTexturedModalRect(x + halfWidthLeft, y, 200 - halfWidthRight, position, halfWidthRight, halfHeightTop);
            drawTexturedModalRect(x + halfWidthLeft, y + halfHeightTop, 200 - halfWidthRight, position + 20 - halfHeightBottom, halfWidthRight, halfHeightBottom);
            drawCenteredString(fontrenderer, displayString, x + width / 2, y + height / 2 - 4, enabled ? 0xFFFFFFFF : 0xFFA0A0A0);
            GlStateManager.enableDepth();
        }
    }
}