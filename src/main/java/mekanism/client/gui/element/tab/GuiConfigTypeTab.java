package mekanism.client.gui.element.tab;

import mekanism.api.transmitters.TransmissionType;
import mekanism.client.gui.GuiSideConfiguration;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiElement;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.sound.SoundHandler;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiConfigTypeTab extends GuiElement {

    private final TransmissionType transmission;
    private boolean visible;
    private boolean left;
    private int yPos;

    private final int ButtonsizeX;

    private final int ButtonsizeY;

    private final int xSize;

    private final int ySize;


    public GuiConfigTypeTab(IGuiWrapper gui, TransmissionType type, ResourceLocation def) {
        super(getResource(), gui, def);
        transmission = type;
        ButtonsizeX = 18;
        ButtonsizeY = 18;
        xSize = 26;
        ySize = 26;
    }

    private static ResourceLocation getResource() {
        return MekanismUtils.getResource(ResourceType.GUI, "Null.png");
    }

    public void setY(int y) {
        yPos = y;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public TransmissionType getTransmissionType() {
        return transmission;
    }

    @Override
    public Rectangle4i getBounds(int guiWidth, int guiHeight) {
        return new Rectangle4i(guiWidth + getLeftBound(false) - 4, guiHeight + yPos, 26, 26);
    }

    @Override
    protected boolean inBounds(int xAxis, int yAxis) {
        return xAxis >= getLeftBound(true) && xAxis <= getRightBound(true) && yAxis >= yPos + 4 && yAxis <= yPos + 22;
    }

    @Override
    public void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight) {
        if (visible) {
            MekanismRenderer.color(getTransmissionType().getColor());
            drawBlack(guiWidth, guiHeight);
            MekanismRenderer.resetColor();
            drawButton(xAxis, yAxis, guiWidth, guiHeight);
            mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.BUTTON_TAB, "button_tab_icon.png"));
            guiObj.drawTexturedRect(guiWidth + getLeftBound(true), guiHeight + yPos + 4, getTransmissionType().getButtonx(), getTransmissionType().getButtony(), 18, 18);
            mc.renderEngine.bindTexture(defaultLocation);
        }
    }

    public void drawButton(int xAxis, int yAxis, int guiWidth, int guiHeight) {
        mc.getTextureManager().bindTexture(MekanismUtils.getResource(MekanismUtils.ResourceType.BUTTON, "Button.png"));
        int i = getHoverState(inBounds(xAxis, yAxis));
        int position = i * 20;
        int halfWidthLeft = ButtonsizeX / 2;
        int halfWidthRight = ButtonsizeX % 2 == 0 ? halfWidthLeft : halfWidthLeft + 1;
        int halfHeightTop = ButtonsizeY / 2;
        int halfHeightBottom = ButtonsizeY % 2 == 0 ? halfHeightTop : halfHeightTop + 1;
        guiObj.drawTexturedRect(guiWidth + getLeftBound(true), guiHeight + yPos + 4, 0, position, halfWidthLeft, halfHeightTop);
        guiObj.drawTexturedRect(guiWidth + getLeftBound(true), guiHeight + yPos + 4 + halfHeightTop, 0, position + 20 - halfHeightBottom, halfWidthLeft, halfHeightBottom);
        guiObj.drawTexturedRect(guiWidth + getLeftBound(true) + halfWidthLeft, guiHeight + yPos + 4, 200 - halfWidthRight, position, halfWidthRight, halfHeightTop);
        guiObj.drawTexturedRect(guiWidth + getLeftBound(true) + halfWidthLeft, guiHeight + yPos + 4 + halfHeightTop, 200 - halfWidthRight, position + 20 - halfHeightBottom, halfWidthRight, halfHeightBottom);
    }

    public void drawBlack(int guiWidth, int guiHeight) {
        mc.getTextureManager().bindTexture(left ? MekanismUtils.getResource(MekanismUtils.ResourceType.BUTTON_TAB, "holder_left.png") : MekanismUtils.getResource(MekanismUtils.ResourceType.BUTTON_TAB, "holder_right.png"));
        int halfWidthLeft = xSize / 2;
        int halfWidthRight = xSize % 2 == 0 ? halfWidthLeft : halfWidthLeft + 1;
        int halfHeightTop = ySize / 2;
        int halfHeight = ySize % 2 == 0 ? halfHeightTop : halfHeightTop + 1;
        guiObj.drawTexturedRect(guiWidth + getLeftBound(false) - 4, guiHeight + yPos, 0, 0, halfWidthLeft, halfHeightTop);
        guiObj.drawTexturedRect(guiWidth + getLeftBound(false) - 4, guiHeight + yPos + halfHeightTop, 0, 256 - halfHeight, halfWidthLeft, halfHeight);
        guiObj.drawTexturedRect(guiWidth + getLeftBound(false) - 4 + halfWidthLeft, guiHeight + yPos, 256 - halfWidthRight, 0, halfWidthRight, halfHeightTop);
        guiObj.drawTexturedRect(guiWidth + getLeftBound(false) - 4 + halfWidthLeft, guiHeight + yPos + halfHeightTop, 256 - halfWidthRight, 256 - halfHeight, halfWidthRight, halfHeight);
    }

    @Override
    public void renderForeground(int xAxis, int yAxis) {
        if (visible) {
            mc.renderEngine.bindTexture(RESOURCE);
            if (inBounds(xAxis, yAxis)) {
                displayTooltip(transmission.localize(), xAxis, yAxis);
            }
            mc.renderEngine.bindTexture(defaultLocation);
        }
    }

    public int getLeftBound(boolean adjust) {
        return left ? -21 + (adjust ? 1 : 0) : 159 - (adjust ? 1 : 0);
    }

    public int getRightBound(boolean adjust) {
        return left ? -3 + (adjust ? 1 : 0) : 177 - (adjust ? 1 : 0);
    }

    @Override
    public void preMouseClicked(int xAxis, int yAxis, int button) {
    }

    @Override
    public void mouseClicked(int xAxis, int yAxis, int button) {
        if (visible && button == 0 && inBounds(xAxis, yAxis)) {
            ((GuiSideConfiguration) guiObj).setCurrentType(transmission);
            ((GuiSideConfiguration) guiObj).updateTabs();
            SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
        }
    }


    protected int getHoverState(boolean hoveredOrFocused) {
        if (hoveredOrFocused) {
            return 2;
        }
        return 1;
    }
}
