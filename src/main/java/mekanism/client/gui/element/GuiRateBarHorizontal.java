package mekanism.client.gui.element;

import mekanism.client.gui.IGuiWrapper;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiRateBarHorizontal extends GuiElement {

    private final int xLocation;
    private final int yLocation;
    private final IRateInfoHandler handler;

    private final RateBarHorizontal type = RateBarHorizontal.HORIZONTAL;

    public GuiRateBarHorizontal(IGuiWrapper gui, IRateInfoHandler h, ResourceLocation def, int x, int y) {
        super(MekanismUtils.getResource(ResourceType.GUI_BAR, "Rate_Bar_Horizontal.png"), gui, def);
        handler = h;
        xLocation = x;
        yLocation = y;
    }

    @Override
    public Rectangle4i getBounds(int guiWidth, int guiHeight) {
        return new Rectangle4i(guiWidth + xLocation, guiHeight + yLocation, type.width, type.height);
    }

    @Override
    protected boolean inBounds(int xAxis, int yAxis) {
        return xAxis >= xLocation + 2 && xAxis <= xLocation + 80 && yAxis >= yLocation + 2 && yAxis <= yLocation + 10;
    }

    @Override
    public void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight) {
        mc.renderEngine.bindTexture(RESOURCE);
        guiObj.drawTexturedRect(guiWidth + xLocation, guiHeight + yLocation, type.textureX, type.textureY, type.width, type.height);
        if (handler.getLevel() > 0) {
            int innerOffsetX = 2;
            int displayInt = (int) (handler.getLevel() * (type.width - 2 * innerOffsetX));
            guiObj.drawTexturedRect(guiWidth + xLocation + innerOffsetX + 1, guiHeight + yLocation, type.textureX + type.width + innerOffsetX, type.textureY, displayInt, type.height);
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

    public static abstract class IRateInfoHandler {

        public String getTooltip() {
            return null;
        }

        public abstract double getLevel();
    }

    public enum RateBarHorizontal {
        HORIZONTAL(84, 12, 0, 0);

        public final int width;
        public final int height;

        public final int textureX;
        public final int textureY;

        RateBarHorizontal(int w, int h, int u, int v) {
            width = w;
            height = h;
            textureX = u;
            textureY = v;
        }
    }

}