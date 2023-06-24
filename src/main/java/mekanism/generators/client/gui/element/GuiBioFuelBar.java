package mekanism.generators.client.gui.element;

import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiElement;
import mekanism.generators.common.util.MekanismGeneratorUtils;
import mekanism.generators.common.util.MekanismGeneratorUtils.ResourceType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBioFuelBar extends GuiElement {

    private final IPowerInfoHandler handler;
    private final int xLocation;
    private final int yLocation;
    private final int width = 6;
    private final int height = 56;


    public GuiBioFuelBar(IGuiWrapper gui, IPowerInfoHandler h, ResourceLocation def, int x, int y) {
        super(MekanismGeneratorUtils.getResource(ResourceType.GUI_ELEMENT, "GuiBioFuelBar.png"), gui, def);
        handler = h;

        xLocation = x;
        yLocation = y;
    }

    @Override
    public Rectangle4i getBounds(int guiWidth, int guiHeight) {
        return new Rectangle4i(guiWidth + xLocation, guiHeight + yLocation, width, height);
    }

    @Override
    protected boolean inBounds(int xAxis, int yAxis) {
        return xAxis >= xLocation && xAxis <= xLocation + width && yAxis >= yLocation && yAxis <= yLocation + height;
    }

    @Override
    public void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight) {
        mc.renderEngine.bindTexture(RESOURCE);
        guiObj.drawTexturedRect(guiWidth + xLocation, guiHeight + yLocation, 0, 0, width, height);
        if (handler.getLevel() > 0) {
            int displayInt = (int) (handler.getLevel() * 52) + 2;
            guiObj.drawTexturedRect(guiWidth + xLocation, guiHeight + yLocation + height - displayInt, 6, height - displayInt, width, displayInt);
        }
        mc.renderEngine.bindTexture(defaultLocation);
    }

    @Override
    public void renderForeground(int xAxis, int yAxis) {
        mc.renderEngine.bindTexture(RESOURCE);
        if (handler.getTooltip() != null && inBounds(xAxis, yAxis)) {
            displayTooltip(handler.getTooltip(), xAxis, yAxis);
        }
        mc.renderEngine.bindTexture(defaultLocation);
    }

    @Override
    public void preMouseClicked(int xAxis, int yAxis, int button) {
    }

    @Override
    public void mouseClicked(int xAxis, int yAxis, int button) {
    }

    public static abstract class IPowerInfoHandler {

        public String getTooltip() {
            return null;
        }

        public abstract double getLevel();
    }
}