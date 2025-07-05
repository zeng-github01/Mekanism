package mekanism.client.newgui.element.bar;

import mekanism.api.energy.IStrictEnergyStorage;
import mekanism.api.text.TextComponentGroup;
import mekanism.client.newgui.IGuiWrapper;
import mekanism.client.newgui.element.bar.GuiBar.IBarInfoHandler;
import mekanism.common.util.MekanismUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import static mekanism.client.gui.element.GuiUtils.blit;

public class GuiHorizontalPowerBar extends GuiBar<IBarInfoHandler> {

    private static final ResourceLocation ENERGY_BAR = MekanismUtils.getResource(MekanismUtils.ResourceType.GUI_BAR, "horizontal_power.png");
    private static final int texWidth = 52;
    private static final int texHeight = 4;


    private final double widthScale;

    public GuiHorizontalPowerBar(IGuiWrapper gui, IStrictEnergyStorage container, int x, int y) {
        this(gui, container, x, y, texWidth);
    }

    public GuiHorizontalPowerBar(IGuiWrapper gui, IStrictEnergyStorage container, int x, int y, int desiredWidth) {
        this(gui, new IBarInfoHandler() {
            @Override
            public ITextComponent getTooltip() {
                return new TextComponentGroup().translation(MekanismUtils.getEnergyDisplay(container.getEnergy(), container.getMaxEnergy()));
            }

            @Override
            public double getLevel() {
                return container.getEnergy() / container.getMaxEnergy();
            }
        }, x, y, desiredWidth);
    }

    public GuiHorizontalPowerBar(IGuiWrapper gui, IBarInfoHandler handler, int x, int y) {
        this(gui, handler, x, y, texWidth);
    }

    public GuiHorizontalPowerBar(IGuiWrapper gui, IBarInfoHandler handler, int x, int y, int desiredWidth) {
        super(ENERGY_BAR, gui, handler, x, y, desiredWidth, texHeight, true);
        widthScale = desiredWidth / (double) texWidth;
    }

    @Override
    protected void renderBarOverlay(int mouseX, int mouseY, float partialTicks, double handlerLevel) {
        int displayInt = (int) (handlerLevel * texWidth);
        if (displayInt > 0) {
            blit(x + 1, y + 1, calculateScaled(widthScale, displayInt), texHeight, 0, 0, displayInt, texHeight, texWidth, texHeight);
        }
    }
}
