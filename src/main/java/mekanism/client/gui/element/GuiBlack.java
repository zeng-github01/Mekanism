package mekanism.client.gui.element;

import mekanism.client.gui.IGuiWrapper;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiBlack extends GuiElement {

    private final int xPosition;
    private final int yPosition;
    private final int xSize;
    private final int ySize;


    public GuiBlack(IGuiWrapper gui, ResourceLocation def, int x, int y, int sizeX, int sizeY) {
        super(MekanismUtils.getResource(ResourceType.GUI_ELEMENT, "GuiBlack.png"), gui, def);
        xPosition = x;
        yPosition = y;
        xSize = sizeX;
        ySize = sizeY;
    }


    @Override
    public Rectangle4i getBounds(int guiWidth, int guiHeight) {
        return new Rectangle4i(guiWidth + xPosition, guiHeight + yPosition, xSize, ySize);
    }

    @Override
    protected boolean inBounds(int xAxis, int yAxis) {
        return xAxis >= xPosition && xAxis <= xPosition + xSize && yAxis >= yPosition && yAxis <= yPosition + ySize;
    }

    @Override
    public void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight) {
        mc.renderEngine.bindTexture(RESOURCE);
        drawBlack(guiWidth, guiHeight);
        mc.renderEngine.bindTexture(defaultLocation);
    }

    @Override
    public void renderForeground(int xAxis, int yAxis) {
    }

    @Override
    public void preMouseClicked(int xAxis, int yAxis, int button) {
    }

    @Override
    public void mouseClicked(int xAxis, int yAxis, int button) {
    }

    public void drawBlack(int guiWidth, int guiHeight) {
        int xDisplays = xSize / 50 + (xSize % 50 > 0 ? 1 : 0);
        int yDisplays = ySize / 50 + (ySize % 50 > 0 ? 1 : 0);

        for (int yIter = 0; yIter < yDisplays; yIter++) {
            for (int xIter = 0; xIter < xDisplays; xIter++) {
                int width = xSize % 50 > 0 && xIter == xDisplays - 1 ? xSize % 50 : 50;
                int height = ySize % 50 > 0 && yIter == yDisplays - 1 ? ySize % 50 : 50;
                guiObj.drawTexturedRect(guiWidth + xPosition + (xIter * 50), guiHeight + yPosition + (yIter * 50), 0, 0, width, height);
            }
        }
    }


}