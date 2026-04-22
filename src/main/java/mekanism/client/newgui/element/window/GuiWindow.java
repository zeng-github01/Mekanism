package mekanism.client.newgui.element.window;

import mekanism.client.gui.element.GuiUtils;
import mekanism.client.newgui.GuiMekanism;
import mekanism.client.newgui.IGuiWrapper;
import mekanism.client.newgui.element.GuiElement;
import mekanism.client.newgui.element.GuiTexturedElement;
import mekanism.client.newgui.element.button.GuiCloseButton;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.inventory.container.IEmptyContainer;
import mekanism.common.inventory.container.SelectedWindowData;
import mekanism.common.inventory.container.SelectedWindowData.WindowType;
import mekanism.common.lib.Color;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class GuiWindow extends GuiTexturedElement {

    private static final Color OVERLAY_COLOR = Color.rgbai(60, 60, 60, 128);

    private final SelectedWindowData windowData;
    private boolean dragging = false;
    private double dragX;
    private double dragY;
    private int prevDX;
    private int prevDY;

    private Consumer<GuiWindow> closeListener;
    private Consumer<GuiWindow> reattachListener;

    protected InteractionStrategy interactionStrategy = InteractionStrategy.CONTAINER;

    private static Pair<Integer, Integer> calculateOpenPosition(IGuiWrapper gui, SelectedWindowData windowData, int x, int y, int width, int height) {
        Pair<Integer, Integer> lastPosition = windowData.getLastPosition();
        ScaledResolution scaledResolution = new ScaledResolution(minecraft);
        int lastX = lastPosition.getLeft();
        if (lastX != Integer.MAX_VALUE) {
            int guiLeft = gui.getLeft();
            if (guiLeft + lastX < 0) {
                lastX = -guiLeft;
            } else if (guiLeft + lastX + width > scaledResolution.getScaledWidth()) {
                lastX = scaledResolution.getScaledWidth() - guiLeft - width;
            }
        }
        int lastY = lastPosition.getRight();
        if (lastY != Integer.MAX_VALUE) {
            int guiTop = gui.getTop();
            if (guiTop + lastY < 0) {
                lastY = -guiTop;
            } else if (guiTop + lastY + height > scaledResolution.getScaledHeight()) {
                lastY = scaledResolution.getScaledHeight() - guiTop - height;
            }
        }
        return Pair.of(lastX == Integer.MAX_VALUE ? x : lastX, lastY == Integer.MAX_VALUE ? y : lastY);
    }

    public GuiWindow(IGuiWrapper gui, int x, int y, int width, int height, WindowType windowType) {
        this(gui, x, y, width, height, windowType == WindowType.UNSPECIFIED ? SelectedWindowData.UNSPECIFIED : new SelectedWindowData(windowType));
    }

    public GuiWindow(IGuiWrapper gui, int x, int y, int width, int height, SelectedWindowData windowData) {
        this(gui, calculateOpenPosition(gui, windowData, x, y, width, height), width, height, windowData);
    }

    private GuiWindow(IGuiWrapper gui, Pair<Integer, Integer> calculatedPosition, int width, int height, SelectedWindowData windowData) {
        super(GuiMekanism.BASE_BACKGROUND, gui, calculatedPosition.getLeft(), calculatedPosition.getRight(), width, height);
        this.windowData = windowData;
        isOverlay = true;
        active = true;
        if (!isFocusOverlay()) {
            addCloseButton();
        }
    }

    public void onFocusLost() {
    }

    public void onFocused() {
        gui().setSelectedWindow(windowData);
    }

    protected void addCloseButton() {
        addChild(new GuiCloseButton(gui(), relativeX + 6, relativeY + 6, this));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean ret = super.mouseClicked(mouseX, mouseY, button);
        if (isMouseOver(mouseX, mouseY)) {
            if (mouseY < y + 18) {
                dragging = true;
                dragX = mouseX;
                dragY = mouseY;
                prevDX = 0;
                prevDY = 0;
            }
        } else if (!ret && interactionStrategy.allowContainer()) {
            if (gui() instanceof GuiMekanism<?> gui) {
                Container container = gui.inventorySlots;
                if (!(container instanceof IEmptyContainer)) {
                    if (mouseX >= getGuiLeft() && mouseX < getGuiLeft() + getGuiWidth() && mouseY >= getGuiTop() + getGuiHeight() - 90) {
                        return false;
                    }
                }
            }
        }
        return ret || !interactionStrategy.allowAll();
    }

    @Override
    public void onDrag(double mouseX, double mouseY, double mouseXOld, double mouseYOld) {
        super.onDrag(mouseX, mouseY, mouseXOld, mouseYOld);
        if (dragging) {
            int newDX = (int) Math.round(mouseX - dragX);
            int newDY = (int) Math.round(mouseY - dragY);
            ScaledResolution scaledResolution = new ScaledResolution(minecraft);
            int changeX = Math.max(-x, Math.min(scaledResolution.getScaledWidth() - (x + width), newDX - prevDX));
            int changeY = Math.max(-y, Math.min(scaledResolution.getScaledHeight() - (y + height), newDY - prevDY));
            prevDX = newDX;
            prevDY = newDY;
            move(changeX, changeY);
        }
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
        dragging = false;
    }

    @Override
    public void renderBackgroundOverlay(int mouseX, int mouseY) {
        if (isFocusOverlay()) {
            ScaledResolution scaledResolution = new ScaledResolution(minecraft);
            MekanismRenderer.renderColorOverlay(0, 0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), OVERLAY_COLOR.rgba());
        } else {
            GlStateManager.color(1, 1, 1, 0.75F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                  GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GuiUtils.renderBackgroundTexture(GuiMekanism.SHADOW, 4, 4, getButtonX() - 3, getButtonY() - 3, getButtonWidth() + 6, getButtonHeight() + 6, 256, 256);
            MekanismRenderer.resetColor();
        }
        minecraft.renderEngine.bindTexture(getResource());
        renderBackgroundTexture(getResource(), 4, 4);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == Keyboard.KEY_RETURN) {
            close();
            return true;
        }
        return false;
    }

    public void setListenerTab(Supplier<? extends GuiElement> elementSupplier) {
        setTabListeners(window -> elementSupplier.get().active = true, window -> elementSupplier.get().active = false);
    }

    public void setTabListeners(Consumer<GuiWindow> closeListener, Consumer<GuiWindow> reattachListener) {
        this.closeListener = closeListener;
        this.reattachListener = reattachListener;
    }

    @Override
    public void resize(int prevLeft, int prevTop, int left, int top) {
        super.resize(prevLeft, prevTop, left, top);
        if (reattachListener != null) {
            reattachListener.accept(this);
        }
    }

    public void renderBlur() {
        GlStateManager.color(1, 1, 1, 0.3F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
              GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GuiUtils.renderBackgroundTexture(GuiMekanism.BLUR, 4, 4, relativeX, relativeY, width, height, 256, 256);
        MekanismRenderer.resetColor();
    }

    public void close() {
        gui().removeWindow(this);
        children.forEach(GuiElement::onWindowClose);
        if (closeListener != null) {
            closeListener.accept(this);
        }
        windowData.updateLastPosition(relativeX, relativeY);
    }

    protected boolean isFocusOverlay() {
        return false;
    }

    @Override
    public void drawTitleText(ITextComponent text, float y) {
        if (isFocusOverlay()) {
            super.drawTitleText(text, y);
        } else {
            int leftShift = getTitlePadStart();
            int xSize = getXSize() - leftShift - getTitlePadEnd();
            int maxLength = xSize - 12;
            float textWidth = getStringWidth(text);
            float scale = Math.min(1, maxLength / textWidth);
            float left = relativeX + xSize / 2F;
            drawScaledCenteredText(text, left + leftShift, relativeY + y, titleTextColor(), scale);
        }
    }

    protected int getTitlePadStart() {
        return 12;
    }

    protected int getTitlePadEnd() {
        return 0;
    }

    public enum InteractionStrategy {
        NONE,
        CONTAINER,
        ALL;

        boolean allowContainer() {
            return this != NONE;
        }

        boolean allowAll() {
            return this == ALL;
        }
    }
}
