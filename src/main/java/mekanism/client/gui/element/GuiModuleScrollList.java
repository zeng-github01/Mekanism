package mekanism.client.gui.element;

import mekanism.api.gear.ModuleData;
import mekanism.api.gear.ModuleData.ExclusiveFlag;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.content.gear.Module;
import mekanism.common.content.gear.ModuleHelper;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GuiModuleScrollList extends GuiElement {

    private static final int ROW_HEIGHT = 12;
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_INNER_WIDTH = 4;
    private static final int SCROLLBAR_HEIGHT = 4;
    private static final int SCROLLBAR_Y_SHIFT = 2;
    private static final ResourceLocation HOLDER = MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "element_holder.png");
    private static final ResourceLocation SCROLL_LIST = MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "scroll_list.png");
    private static final ResourceLocation MODULE_SELECTION = MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "module_selection.png");
    private static final int HOLDER_TEXTURE_SIZE = 256;
    private static final int HOLDER_TEXTURE_SIDE = 32;
    private static final int SCROLL_TEXTURE_WIDTH = 6;
    private static final int SCROLL_TEXTURE_HEIGHT = 6;
    private static final int SELECTION_TEXTURE_WIDTH = 100;
    private static final int SELECTION_TEXTURE_HEIGHT = 36;
    private static final int SELECTION_TEXTURE_ROW_HEIGHT = SELECTION_TEXTURE_HEIGHT / 3;

    private final int xPosition;
    private final int yPosition;
    private final int xSize;
    private final int ySize;
    private final Supplier<ItemStack> itemSupplier;
    private final Consumer<Module<?>> callback;

    private final List<Module<?>> currentList = new ArrayList<>();
    private ItemStack currentItem = ItemStack.EMPTY;
    private boolean isDragging;
    private int dragOffset;
    private float scroll;
    @Nullable
    private ModuleData<?> selectedType;

    public GuiModuleScrollList(IGuiWrapper gui, ResourceLocation def, int x, int y, int sizeX, int sizeY, Supplier<ItemStack> itemSupplier, Consumer<Module<?>> callback) {
        super(def, gui, def);
        xPosition = x;
        yPosition = y;
        xSize = sizeX;
        ySize = sizeY;
        this.itemSupplier = itemSupplier;
        this.callback = callback;
        updateItemAndList(itemSupplier.get());
    }

    @Override
    public Rectangle4i getBounds(int guiWidth, int guiHeight) {
        return new Rectangle4i(guiWidth + xPosition, guiHeight + yPosition, xSize, ySize);
    }

    @Override
    public void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight) {
        recheckItem();
        int left = guiWidth + xPosition;
        int top = guiHeight + yPosition;
        int barX = left + getBarXOffset();
        int barY = top + SCROLLBAR_Y_SHIFT;
        int scrollIndex = getScrollIndex();
        int hoveredIndex = getIndexAt(xAxis, yAxis);

        GuiUtils.renderBackgroundTexture(HOLDER, HOLDER_TEXTURE_SIDE, HOLDER_TEXTURE_SIDE, left, top, xSize, ySize, HOLDER_TEXTURE_SIZE, HOLDER_TEXTURE_SIZE);
        mc.renderEngine.bindTexture(SCROLL_LIST);
        GuiUtils.blit(barX - 1, barY - 1, 0, 0, SCROLL_TEXTURE_WIDTH, 1, SCROLL_TEXTURE_WIDTH, SCROLL_TEXTURE_HEIGHT);
        GuiUtils.blit(barX - 1, barY, SCROLL_TEXTURE_WIDTH, getMaxBarHeight(), 0, 1, SCROLL_TEXTURE_WIDTH, 1, SCROLL_TEXTURE_WIDTH, SCROLL_TEXTURE_HEIGHT);
        GuiUtils.blit(barX - 1, top + getMaxBarHeight() + 2, 0, 0, SCROLL_TEXTURE_WIDTH, 1, SCROLL_TEXTURE_WIDTH, SCROLL_TEXTURE_HEIGHT);
        GuiUtils.blit(barX, barY + getHandleOffset(), 0, 2, SCROLLBAR_INNER_WIDTH, SCROLLBAR_HEIGHT, SCROLL_TEXTURE_WIDTH, SCROLL_TEXTURE_HEIGHT);

        mc.renderEngine.bindTexture(MODULE_SELECTION);
        for (int row = 0; row < getVisibleRows(); row++) {
            int index = scrollIndex + row;
            if (index >= currentList.size()) {
                break;
            }
            int rowTop = top + (row * ROW_HEIGHT) + 1;
            ModuleData<?> moduleData = currentList.get(index).getData();
            int state = 1;
            if (moduleData == selectedType) {
                state = 2;
            } else if (index == hoveredIndex) {
                state = 0;
            }
            drawSelectionRow(left + 1, rowTop, SELECTION_TEXTURE_WIDTH, ROW_HEIGHT, state);
            MekanismRenderer.resetColor();
        }
        mc.renderEngine.bindTexture(defaultLocation);
        MekanismRenderer.resetColor();
    }

    @Override
    public void renderForeground(int xAxis, int yAxis) {
        int scrollIndex = getScrollIndex();
        int hoveredIndex = getIndexAt(xAxis, yAxis);
        for (int row = 0; row < getVisibleRows(); row++) {
            int index = scrollIndex + row;
            if (index >= currentList.size()) {
                break;
            }
            Module<?> module = currentList.get(index);
            int rowY = yPosition + (row * ROW_HEIGHT) + 1;
            GuiUtils.renderItem(mc.getRenderItem(), module.getData().getStack(), xPosition + 3, rowY + 2, 0.5F, getFontRenderer(), null, false);

            int color = module.getData().isExclusive(ExclusiveFlag.ANY) ? (module.isEnabled() ? 0x635BD4 : 0x2E2A69) : (module.isEnabled() ? 0xFF3CFE9A : 0xFF5E1D1D);
            renderScaledText(getModuleName(module.getData()), xPosition + 13, rowY + 2, color, xSize - SCROLLBAR_WIDTH - 17);
        }

        if (hoveredIndex >= 0 && hoveredIndex < currentList.size()) {
            Module<?> module = currentList.get(hoveredIndex);
            List<String> tooltip = new ArrayList<>(2);
            tooltip.add(getModuleName(module.getData()));
            tooltip.add(LangUtils.localize("module.mekanism.installed") + " " + module.getInstalledCount() + "/" + module.getData().getMaxStackSize());
            displayTooltips(tooltip, xAxis, yAxis);
        }
        MekanismRenderer.resetColor();
    }

    @Override
    public void preMouseClicked(int xAxis, int yAxis, int button) {
    }

    @Override
    public void mouseClicked(int xAxis, int yAxis, int button) {
        if (button != 0) {
            return;
        }
        if (isInScrollbar(xAxis, yAxis)) {
            if (canScroll()) {
                int handleTop = yPosition + SCROLLBAR_Y_SHIFT + getHandleOffset();
                if (yAxis >= handleTop && yAxis < handleTop + SCROLLBAR_HEIGHT) {
                    dragOffset = yAxis - handleTop;
                    isDragging = true;
                }
            } else {
                scroll = 0;
            }
            return;
        }

        if (isInContent(xAxis, yAxis)) {
            int index = getIndexAt(xAxis, yAxis);
            if (index >= 0 && index < currentList.size()) {
                setSelected(currentList.get(index).getData());
            } else {
                clearSelection();
            }
        }
    }

    @Override
    public void mouseClickMove(int xAxis, int yAxis, int button, long ticks) {
        if (isDragging) {
            setScrollFromMouse(yAxis);
        }
    }

    @Override
    public void mouseReleased(int xAxis, int yAxis, int type) {
        if (type == 0) {
            isDragging = false;
            dragOffset = 0;
        }
    }

    @Override
    public void mouseWheel(int xAxis, int yAxis, int delta) {
        if (canScroll() && inBounds(xAxis, yAxis)) {
            int elements = currentList.size() - getVisibleRows();
            if (elements > 0) {
                float adjustedDelta = delta > 0 ? 1 : -1;
                scroll = clamp(scroll - adjustedDelta / elements);
            }
        }
    }

    @Override
    protected boolean inBounds(int xAxis, int yAxis) {
        return xAxis >= xPosition && xAxis < xPosition + xSize && yAxis >= yPosition && yAxis < yPosition + ySize;
    }

    public void clearSelection() {
        if (selectedType != null) {
            selectedType = null;
            onSelectedChange();
        }
    }

    private void recheckItem() {
        ItemStack stack = itemSupplier.get();
        if (!sameStack(currentItem, stack)) {
            ModuleData<?> previousSelection = selectedType;
            updateItemAndList(stack);
            if (previousSelection != null) {
                if (getModule(previousSelection) != null) {
                    onSelectedChange();
                } else {
                    clearSelection();
                }
            }
        }
    }

    private void updateItemAndList(ItemStack stack) {
        currentItem = stack;
        currentList.clear();
        if (!stack.isEmpty()) {
            currentList.addAll(ModuleHelper.get().loadAll(stack));
            currentList.sort(Comparator.comparing(module -> getModuleName(module.getData()), String.CASE_INSENSITIVE_ORDER));
        }
        if (!canScroll()) {
            scroll = 0;
        } else {
            scroll = clamp(scroll);
        }
    }

    private void onSelectedChange() {
        callback.accept(getModule(selectedType));
    }

    @Nullable
    private Module<?> getModule(@Nullable ModuleData<?> data) {
        if (data == null) {
            return null;
        }
        for (Module<?> module : currentList) {
            if (module.getData() == data) {
                return module;
            }
        }
        return null;
    }

    private void setSelected(@Nullable ModuleData<?> moduleData) {
        if (selectedType != moduleData) {
            selectedType = moduleData;
            onSelectedChange();
        }
    }

    private int getVisibleHeight() {
        return getVisibleRows() * ROW_HEIGHT;
    }

    private int getVisibleRows() {
        return Math.max(1, (ySize - 2) / ROW_HEIGHT);
    }

    private boolean canScroll() {
        return currentList.size() > getVisibleRows();
    }

    private int getMaxScrollIndex() {
        return Math.max(1, currentList.size() - getVisibleRows());
    }

    private int getScrollIndex() {
        if (canScroll()) {
            int size = currentList.size() - getVisibleRows();
            return Math.min(size, (int) ((size + 0.5F) * scroll));
        }
        return 0;
    }

    private int getBarXOffset() {
        return xSize - SCROLLBAR_WIDTH;
    }

    private int getMaxBarHeight() {
        return ySize - 4;
    }

    private int getHandleOffset() {
        int maxOffset = getMaxBarHeight() - SCROLLBAR_HEIGHT;
        return Math.max(Math.min((int) (scroll * maxOffset), maxOffset), 0);
    }

    private void setScrollFromMouse(int yAxis) {
        int maxOffset = getMaxBarHeight() - SCROLLBAR_HEIGHT;
        if (maxOffset == 0) {
            scroll = 0;
            return;
        }
        scroll = clamp((float) (yAxis - (yPosition + SCROLLBAR_Y_SHIFT) - dragOffset) / maxOffset);
    }

    private int getIndexAt(int xAxis, int yAxis) {
        if (!isInContent(xAxis, yAxis)) {
            return -1;
        }
        int row = (yAxis - yPosition - 1) / ROW_HEIGHT;
        int index = getScrollIndex() + row;
        return index < currentList.size() ? index : -1;
    }

    private boolean isInContent(int xAxis, int yAxis) {
        return xAxis >= xPosition + 1 && xAxis < xPosition + getBarXOffset() - 1 && yAxis >= yPosition + 1 && yAxis < yPosition + ySize - 1;
    }

    private boolean isInScrollbar(int xAxis, int yAxis) {
        int barX = xPosition + getBarXOffset();
        int barY = yPosition + SCROLLBAR_Y_SHIFT + getHandleOffset();
        return xAxis >= barX && xAxis < barX + SCROLLBAR_INNER_WIDTH && yAxis >= barY && yAxis < barY + SCROLLBAR_HEIGHT;
    }

    private float clamp(float value) {
        return Math.max(0, Math.min(1, value));
    }

    private boolean sameStack(ItemStack first, ItemStack second) {
        if (first.isEmpty()) {
            return second.isEmpty();
        } else if (second.isEmpty()) {
            return false;
        }
        return first.getCount() == second.getCount() && ItemStack.areItemsEqual(first, second) && ItemStack.areItemStackTagsEqual(first, second);
    }

    private String getModuleName(ModuleData<?> moduleData) {
        String translationKey = moduleData.getTranslationKey();
        return LangUtils.canLocalize(translationKey) ? LangUtils.localize(translationKey) : moduleData.getStack().getDisplayName();
    }

    private void drawSelectionRow(int x, int y, int width, int height, int state) {
        GuiUtils.blit(x, y, width, height, 0, SELECTION_TEXTURE_ROW_HEIGHT * state, SELECTION_TEXTURE_WIDTH, SELECTION_TEXTURE_ROW_HEIGHT, SELECTION_TEXTURE_WIDTH,
              SELECTION_TEXTURE_HEIGHT);
    }
}
