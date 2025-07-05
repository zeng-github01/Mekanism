package mekanism.client.newgui.element.button;

import mekanism.client.newgui.element.window.GuiWindow;
import mekanism.client.newgui.IGuiWrapper;

import mekanism.common.MekanismLang;
import mekanism.common.util.MekanismUtils;

public class GuiCloseButton extends MekanismImageButton {

    public GuiCloseButton(IGuiWrapper gui, int x, int y, GuiWindow window) {
        super(gui, x, y, 8, MekanismUtils.getResource(MekanismUtils.ResourceType.GUI_BUTTON, "close.png"), window::close);
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        super.renderToolTip(mouseX, mouseY);
        displayTooltip(MekanismLang.CLOSE.translate(), mouseX, mouseY);
    }

    @Override
    public boolean resetColorBeforeRender() {
        return false;
    }
}
