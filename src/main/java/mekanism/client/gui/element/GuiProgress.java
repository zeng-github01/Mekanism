package mekanism.client.gui.element;

import mekanism.client.gui.IGuiWrapper;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiProgress extends GuiElement {

    protected final IProgressInfoHandler handler;
    protected final ProgressBar type;
    protected final int xLocation;
    protected final int yLocation;

    public GuiProgress(IProgressInfoHandler handler, ProgressBar type, IGuiWrapper gui, ResourceLocation def, int x, int y) {
        super(MekanismUtils.getResource(ResourceType.PROGRESS, "Progress_Icon.png"), gui, def);
        xLocation = x;
        yLocation = y;

        this.type = type;
        this.handler = handler;
    }


    @Override
    public Rectangle4i getBounds(int guiWidth, int guiHeight) {
        return new Rectangle4i(guiWidth + xLocation, guiHeight + yLocation, type.width, type.height);
    }

    @Override
    public void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight) {
        mc.renderEngine.bindTexture(RESOURCE);
        if (handler.isActive()) {
            guiObj.drawTexturedRect(guiWidth + xLocation, guiHeight + yLocation, type.textureX, type.textureY, type.width, type.height);
            int innerOffsetX = 2;
            if (type.vertical) {
                int displayInt = (int) (handler.getProgress() * type.height);
                if (displayInt > 0) {
                    int innerOffsetY = 0;
                    if (type.isReverse()) {
                        innerOffsetY += type.height - displayInt;
                    }
                    guiObj.drawTexturedRect(guiWidth + xLocation + innerOffsetX, guiHeight + yLocation + innerOffsetY, type.textureX + type.width + innerOffsetX, type.textureY, type.width, displayInt);
                }
            } else {
                int displayInt = (int) (handler.getProgress() * (type.width - 2 * innerOffsetX));
                if (displayInt > 0) {
                    if (type.isReverse()) {
                        innerOffsetX += type.width - 4 - displayInt;
                    }
                    guiObj.drawTexturedRect(guiWidth + xLocation + innerOffsetX, guiHeight + yLocation, type.textureX + type.width + innerOffsetX, type.textureY, displayInt, type.height);
                }
            }
        }
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

    public enum ProgressBar {
        BAR(28, 11, 0, 0, false),
        LARGE_RIGHT(52, 10, 128, 0, false),
        LARGE_LEFT(52, 10, 128, 10, false),
        MEDIUM(36, 10, 128, 20, false),
        SMALL_RIGHT(32, 10, 128, 30, false),
        SMALL_LEFT(32, 10, 128, 40, false),
        BI(20, 8, 128, 50, false),
        DOWN(12, 22, 128, 58, true),
        TALL_RIGHT(26,17,128,80,false),
        INSTALLING(14,16,128,97,true);

        public final int width;
        public final int height;

        public final int textureX;
        public final int textureY;

        public final boolean vertical;

        ProgressBar(int w, int h, int u, int v, boolean vertical) {
            width = w;
            height = h;
            textureX = u;
            textureY = v;
            this.vertical = vertical;
        }

        public boolean isReverse() {
            return this == SMALL_LEFT || this == LARGE_LEFT;
        }
    }


    public abstract static class IProgressInfoHandler {

        public abstract double getProgress();

        public boolean isActive() {
            return true;
        }
    }
}
