package mekanism.client.newgui.element.bar;

import mekanism.client.newgui.IGuiWrapper;
import mekanism.client.newgui.element.bar.GuiBar.IBarInfoHandler;
import mekanism.common.util.MekanismUtils;
import net.minecraft.util.ResourceLocation;

import static mekanism.client.gui.element.GuiUtils.blit;

public class GuiHorizontalRateBar extends GuiBar<IBarInfoHandler> {

    private static final ResourceLocation RATE_BAR = MekanismUtils.getResource(MekanismUtils.ResourceType.GUI_BAR, "horizontal_rate.png");
    private static final int texWidth = 78;
    private static final int texHeight = 8;

    public GuiHorizontalRateBar(IGuiWrapper gui, IBarInfoHandler handler, int x, int y) {
        super(RATE_BAR, gui, handler, x, y, texWidth, texHeight, true);
    }

    @Override
    protected void renderBarOverlay(int mouseX, int mouseY, float partialTicks, double handlerLevel) {
        int displayInt = (int) (handlerLevel * texWidth);
        if (displayInt > 0) {
            blit(x + 1, y + 1, 0, 0, displayInt, texHeight, texWidth, texHeight);
        }
    }
}
