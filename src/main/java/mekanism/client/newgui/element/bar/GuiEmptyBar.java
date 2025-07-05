package mekanism.client.newgui.element.bar;

import mekanism.client.newgui.IGuiWrapper;
import mekanism.client.newgui.element.bar.GuiBar.IBarInfoHandler;

public class GuiEmptyBar extends GuiBar<IBarInfoHandler> {

    private static final IBarInfoHandler EMPTY_INFO = () -> 0;

    public GuiEmptyBar(IGuiWrapper gui, int x, int y, int width, int height) {
        super(null, gui, EMPTY_INFO, x, y, width, height, width >= height);
        //Pass null as we technically don't need a resource given our handler always gives a zero size
    }

    @Override
    protected void renderBarOverlay(int mouseX, int mouseY, float partialTicks, double handlerLevel) {
    }
}
