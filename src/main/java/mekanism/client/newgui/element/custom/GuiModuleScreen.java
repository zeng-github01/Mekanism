package mekanism.client.newgui.element.custom;


import mekanism.api.gear.IModule;
import mekanism.api.gear.ModuleData.ExclusiveFlag;
import mekanism.api.gear.config.ModuleBooleanData;
import mekanism.api.gear.config.ModuleColorData;
import mekanism.api.gear.config.ModuleConfigData;
import mekanism.api.gear.config.ModuleEnumData;
import mekanism.api.text.IHasTextComponent;
import mekanism.api.text.TextComponentGroup;
import mekanism.client.gui.element.GuiUtils;
import mekanism.client.newgui.GuiModuleTweaker;
import mekanism.client.newgui.IGuiWrapper;
import mekanism.client.newgui.element.GuiElement;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.MekanismSounds;
import mekanism.common.content.gear.Module;
import mekanism.common.content.gear.ModuleConfigItem;
import mekanism.common.network.PacketUpdateModuleSettings;
import mekanism.client.newgui.element.window.GuiColorWindow;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.common.util.text.TextUtils;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

public class GuiModuleScreen extends GuiElement {

    private static final ResourceLocation RADIO = MekanismUtils.getResource(ResourceType.GUI, "radio_button.png");
    private static final ResourceLocation SLIDER = MekanismUtils.getResource(ResourceType.GUI, "slider.png");
    public static final ResourceLocation SCREEN = MekanismUtils.getResource(ResourceType.GUI, "inner_screen.png");
    private static final int COLOR_HIGHLIGHT = 0x332A7FFF;
    private static final int COLOR_SELECTION_BORDER = 0xFF5B84FF;
    private static final int COLOR_DEFAULT_BORDER = 0xFF30353D;
    private final int TEXT_COLOR = screenTextColor();

    private final IntSupplier slotIdSupplier;
    @Nullable
    private final GuiModuleTweaker.ArmorPreview armorPreview;

    private IModule<?> currentModule;
    private List<MiniElement> miniElements = new ArrayList<>();
    @Nullable
    private SelectedColorConfig selectedColorConfig;

    public GuiModuleScreen(IGuiWrapper gui, int x, int y, IntSupplier slotIdSupplier, @Nullable GuiModuleTweaker.ArmorPreview armorPreview) {
        super(gui, x, y, 102, 204);
        this.slotIdSupplier = slotIdSupplier;
        this.armorPreview = armorPreview;
    }

    private Runnable getCallback(ModuleConfigData<?> configData, int dataIndex) {
        return () -> {
            if (currentModule != null) {
                Mekanism.packetHandler.sendToServer(PacketUpdateModuleSettings.create(slotIdSupplier.getAsInt(), currentModule.getData(), dataIndex, configData));
            }
        };
    }

    @Nullable
    public IModule<?> getCurrentModule() {
        return currentModule;
    }

    @Nullable
    public SelectedColorConfig getSelectedColorConfig() {
        return selectedColorConfig;
    }

    @SuppressWarnings("unchecked")
    public void setModule(Module<?> module) {
        List<MiniElement> newElements = new ArrayList<>();
        SelectedColorConfig previousColor = currentModule != null && module != null && currentModule.getData() == module.getData() ? selectedColorConfig : null;
        SelectedColorConfig firstColor = null;
        SelectedColorConfig selectedColor = null;

        if (module != null) {
            int startY = 3;
            if (module.getData().isExclusive(ExclusiveFlag.ANY)) {
                startY += 13;
            }
            if (module.getData().getMaxStackSize() > 1) {
                startY += 13;
            }
            List<ModuleConfigItem<?>> configItems = module.getConfigItems();
            for (int i = 0, configItemsCount = configItems.size(); i < configItemsCount; i++) {
                ModuleConfigItem<?> configItem = configItems.get(i);
                if (configItem.getData() instanceof ModuleBooleanData && (!configItem.getName().equals(Module.ENABLED_KEY) || !module.getData().isNoDisable())) {
                    if (configItem instanceof ModuleConfigItem.DisableableModuleConfigItem && !((ModuleConfigItem.DisableableModuleConfigItem) configItem).isConfigEnabled()) {
                        continue;
                    }
                    newElements.add(new BooleanToggle((ModuleConfigItem<Boolean>) configItem, 2, startY, i));
                    startY += 24;
                } else if (configItem.getData() instanceof ModuleEnumData) {
                    EnumToggle toggle = new EnumToggle((ModuleConfigItem<Enum<? extends IHasTextComponent>>) configItem, 2, startY, i);
                    newElements.add(toggle);
                    startY += 34;
                    if (currentModule != null && currentModule.getData() == module.getData() && i < miniElements.size() && miniElements.get(i) instanceof EnumToggle) {
                        toggle.dragging = ((EnumToggle) miniElements.get(i)).dragging;
                    }
                } else if (configItem.getData() instanceof ModuleColorData data) {
                    SelectedColorConfig colorConfig = new SelectedColorConfig((ModuleConfigItem<Integer>) configItem, i, data.handlesAlpha(), getCallback(configItem.getData(), i));
                    if (firstColor == null) {
                        firstColor = colorConfig;
                    }
                    if (previousColor != null && previousColor.matches(configItem, i)) {
                        selectedColor = colorConfig;
                    }
                    newElements.add(new ColorSelection(colorConfig, 2, startY, i));
                    startY += 24;
                }
            }
        }

        currentModule = module;
        miniElements = newElements;
        selectedColorConfig = selectedColor == null ? firstColor : selectedColor;
    }

    @Override
    public void drawBackground(int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(mouseX, mouseY, partialTicks);
        renderBackgroundTexture(SCREEN, 32, 32);
        miniElements.forEach(element -> element.renderBackground(mouseX, mouseY));
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        miniElements.forEach(element -> element.click(mouseX, mouseY));
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        super.onRelease(mouseX, mouseY);
        miniElements.forEach(element -> element.release(mouseX, mouseY));
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        super.renderForeground(mouseX, mouseY);

        if (currentModule != null) {
            int startY = relativeY + 5;
            if (currentModule.getData().isExclusive(ExclusiveFlag.ANY)) {
                ITextComponent comp = MekanismLang.MODULE_EXCLUSIVE.translate();
                drawTextWithScale(comp, relativeX + 5, startY, 0x635BD4, 0.8F);
                startY += 13;
            }
            if (currentModule.getData().getMaxStackSize() > 1) {
                drawTextWithScale(new TextComponentGroup().translation(MekanismLang.MODULE_INSTALLED.getTranslationKey()).translation(currentModule.getInstalledCount() + ""),
                      relativeX + 5, startY, TEXT_COLOR, 0.8F);
                startY += 13;
            }
        }
        miniElements.forEach(element -> element.renderForeground(mouseX, mouseY));
    }

    private void fillRect(int x, int y, int width, int height, int color) {
        GuiUtils.fill(x, y, x + width, y + height, color);
    }

    private void drawOutline(int x, int y, int width, int height, int color) {
        fillRect(x, y, width, 1, color);
        fillRect(x, y + height - 1, width, 1, color);
        if (height > 2) {
            fillRect(x, y + 1, 1, height - 2, color);
            fillRect(x + width - 1, y + 1, 1, height - 2, color);
        }
    }

    private void playSelectionSound() {
        minecraft.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(MekanismSounds.BEEP, 1.0F));
    }

    public static class SelectedColorConfig {

        private final ModuleConfigItem<Integer> data;
        private final int dataIndex;
        private final boolean handlesAlpha;
        private final Runnable saveCallback;

        private SelectedColorConfig(ModuleConfigItem<Integer> data, int dataIndex, boolean handlesAlpha, Runnable saveCallback) {
            this.data = data;
            this.dataIndex = dataIndex;
            this.handlesAlpha = handlesAlpha;
            this.saveCallback = saveCallback;
        }

        public ITextComponent getDescription() {
            return data.getDescription();
        }

        public int getColor() {
            return data.get();
        }

        public void setColor(int color) {
            data.set(color);
        }

        public boolean handlesAlpha() {
            return handlesAlpha;
        }

        public void save() {
            saveCallback.run();
        }

        private boolean matches(ModuleConfigItem<?> otherData, int otherDataIndex) {
            return dataIndex == otherDataIndex && data.getName().equals(otherData.getName());
        }
    }

    abstract class MiniElement {

        final int xPos;
        final int yPos;
        final int dataIndex;

        MiniElement(int xPos, int yPos, int dataIndex) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.dataIndex = dataIndex;
        }

        abstract void renderBackground(int mouseX, int mouseY);

        abstract void renderForeground(int mouseX, int mouseY);

        abstract void click(double mouseX, double mouseY);

        void release(double mouseX, double mouseY) {
        }

        boolean mouseOver(double mouseX, double mouseY, int offsetX, int offsetY, int width, int height) {
            return mouseX >= getX() + offsetX && mouseX < getX() + offsetX + width && mouseY >= getY() + offsetY && mouseY < getY() + offsetY + height;
        }

        int getRelativeX() {
            return relativeX + xPos;
        }

        int getRelativeY() {
            return relativeY + yPos;
        }

        int getX() {
            return x + xPos;
        }

        int getY() {
            return y + yPos;
        }
    }

    class BooleanToggle extends MiniElement {

        final ModuleConfigItem<Boolean> data;

        BooleanToggle(ModuleConfigItem<Boolean> data, int xPos, int yPos, int dataIndex) {
            super(xPos, yPos, dataIndex);
            this.data = data;
        }

        @Override
        public void renderBackground(int mouseX, int mouseY) {
            minecraft.renderEngine.bindTexture(RADIO);

            boolean hover = mouseX >= getX() + 4 && mouseX < getX() + 12 && mouseY >= getY() + 11 && mouseY < getY() + 19;
            if (data.get()) {
                GuiUtils.blit(getX() + 4, getY() + 11, 0, 8, 8, 8, 16, 16);
            } else {
                GuiUtils.blit(getX() + 4, getY() + 11, hover ? 8 : 0, 0, 8, 8, 16, 16);
            }
            hover = mouseX >= getX() + 50 && mouseX < getX() + 58 && mouseY >= getY() + 11 && mouseY < getY() + 19;
            if (!data.get()) {
                GuiUtils.blit(getX() + 50, getY() + 11, 8, 8, 8, 8, 16, 16);
            } else {
                GuiUtils.blit(getX() + 50, getY() + 11, hover ? 8 : 0, 0, 8, 8, 16, 16);
            }
        }

        @Override
        public void renderForeground(int mouseX, int mouseY) {
            drawTextWithScale(data.getDescription(), getRelativeX() + 3, getRelativeY(), TEXT_COLOR, 0.8F);
            drawTextWithScale(MekanismLang.TRUE.translate(), getRelativeX() + 16, getRelativeY() + 11, TEXT_COLOR, 0.8F);
            drawTextWithScale(MekanismLang.FALSE.translate(), getRelativeX() + 62, getRelativeY() + 11, TEXT_COLOR, 0.8F);
        }

        @Override
        public void click(double mouseX, double mouseY) {
            if (!data.get() && mouseOver(mouseX, mouseY, 4, 11, 8, 8)) {
                data.set(true, getCallback(data.getData(), dataIndex));
                playSelectionSound();
            }

            if (data.get() && mouseOver(mouseX, mouseY, 50, 11, 8, 8)) {
                data.set(false, getCallback(data.getData(), dataIndex));
                playSelectionSound();
            }
        }
    }

    class EnumToggle extends MiniElement {

        final int BAR_LENGTH = getWidth() - 24;
        final int BAR_START = 10;
        final float TEXT_SCALE = 0.7F;
        final ModuleConfigItem<Enum<? extends IHasTextComponent>> data;
        boolean dragging = false;

        EnumToggle(ModuleConfigItem<Enum<? extends IHasTextComponent>> data, int xPos, int yPos, int dataIndex) {
            super(xPos, yPos, dataIndex);
            this.data = data;
        }

        @Override
        public void renderBackground(int mouseX, int mouseY) {
            minecraft.renderEngine.bindTexture(SLIDER);
            int count = ((ModuleEnumData<?>) data.getData()).getEnums().size();
            int center = (BAR_LENGTH / (count - 1)) * data.get().ordinal();
            GuiUtils.blit(getX() + BAR_START + center - 2, getY() + 11, 0, 0, 5, 6, 8, 8);
            GuiUtils.blit(getX() + BAR_START, getY() + 17, 0, 6, BAR_LENGTH, 2, 8, 8);
        }

        @Override
        public void renderForeground(int mouseX, int mouseY) {
            ModuleEnumData<?> enumData = (ModuleEnumData<?>) data.getData();
            drawTextWithScale(data.getDescription(), getRelativeX() + 3, getRelativeY(), TEXT_COLOR, 0.8F);
            List<? extends Enum<? extends IHasTextComponent>> options = enumData.getEnums();
            int count = options.size();
            for (int i = 0; i < count; i++) {
                int diffFromCenter = ((BAR_LENGTH / (count - 1)) * i) - (BAR_LENGTH / 2);
                float diffScale = 1 - (1 - TEXT_SCALE) / 2F;
                int textCenter = getRelativeX() + BAR_START + (BAR_LENGTH / 2) + (int) (diffFromCenter * diffScale);
                drawScaledCenteredText(((IHasTextComponent) options.get(i)).getTextComponent(), textCenter, getRelativeY() + 20, TEXT_COLOR, TEXT_SCALE);
            }

            if (dragging) {
                int cur = (int) Math.round(((double) (mouseX - getX() - BAR_START) / (double) BAR_LENGTH) * (count - 1));
                cur = Math.min(count - 1, Math.max(0, cur));
                if (cur != data.get().ordinal()) {
                    data.set(options.get(cur), getCallback(data.getData(), dataIndex));
                }
            }
        }

        @Override
        public void click(double mouseX, double mouseY) {
            List<? extends Enum<? extends IHasTextComponent>> options = ((ModuleEnumData<?>) data.getData()).getEnums();
            if (!dragging) {
                int center = (BAR_LENGTH / (options.size() - 1)) * data.get().ordinal();
                if (mouseOver(mouseX, mouseY, BAR_START + center - 2, 11, 5, 6)) {
                    dragging = true;
                }
            }
            if (!dragging && mouseOver(mouseX, mouseY, BAR_START, 10, BAR_LENGTH, 12)) {
                int count = options.size();
                int cur = (int) Math.round(((mouseX - getX() - BAR_START) / BAR_LENGTH) * (count - 1));
                cur = Math.min(count - 1, Math.max(0, cur));
                if (cur != data.get().ordinal()) {
                    data.set(options.get(cur), getCallback(data.getData(), dataIndex));
                }
            }
        }

        @Override
        public void release(double mouseX, double mouseY) {
            dragging = false;
        }
    }

    class ColorSelection extends MiniElement {

        private static final int SWATCH_SIZE = 18;
        private final SelectedColorConfig selection;

        ColorSelection(SelectedColorConfig selection, int xPos, int yPos, int dataIndex) {
            super(xPos, yPos, dataIndex);
            this.selection = selection;
        }

        @Override
        void renderBackground(int mouseX, int mouseY) {
            int swatchX = getX() + GuiModuleScreen.this.getWidth() - SWATCH_SIZE - 7;
            int swatchY = getY() + 3;
            if (isSelected()) {
                fillRect(getX() + 1, getY() + 10, GuiModuleScreen.this.getWidth() - 7, 11, COLOR_HIGHLIGHT);
            }
            drawOutline(swatchX, swatchY, SWATCH_SIZE, SWATCH_SIZE, isSelected() ? COLOR_SELECTION_BORDER : COLOR_DEFAULT_BORDER);
            fillRect(swatchX + 1, swatchY + 1, SWATCH_SIZE - 2, SWATCH_SIZE - 2, selection.getColor());
        }

        @Override
        void renderForeground(int mouseX, int mouseY) {
            drawTextWithScale(selection.getDescription(), getRelativeX() + 3, getRelativeY(), TEXT_COLOR, 0.8F);
            int color = selection.getColor();
            String hex = selection.handlesAlpha() ? TextUtils.hex(false, 4, color) : TextUtils.hex(false, 3, color & 0xFFFFFF);
            drawTextExact(MekanismLang.GENERIC_HEX.translate(hex), getRelativeX() + 3, getRelativeY() + 11, TEXT_COLOR);
        }

        @Override
        void click(double mouseX, double mouseY) {
            if (mouseOver(mouseX, mouseY, 0, 0, GuiModuleScreen.this.getWidth() - 6, 22) && !isSelected()) {
                selectedColorConfig = selection;
                playSelectionSound();
            }
            if (mouseOver(mouseX, mouseY, 0, 0, GuiModuleScreen.this.getWidth() - 6, 22)) {
                int windowWidth = GuiColorWindow.getWindowWidth(selection.handlesAlpha(), armorPreview != null);
                int windowHeight = GuiColorWindow.getWindowHeight(selection.handlesAlpha());
                int windowX = getGuiWidth() / 2 - windowWidth / 2;
                int windowY = getGuiHeight() / 2 - windowHeight / 2;
                IModule<?> selectedModule = currentModule;
                Runnable previewUpdater = armorPreview != null && selectedModule != null ? () -> armorPreview.tryUpdateFull(selectedModule.getContainer()) : null;
                gui().addWindow(new GuiColorWindow(gui(), windowX, windowY, selection, previewUpdater, armorPreview));
            }
        }

        private boolean isSelected() {
            return selectedColorConfig != null && selectedColorConfig.dataIndex == dataIndex;
        }
    }
}
