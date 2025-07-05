package mekanism.client.newgui.element.scroll;

import mekanism.api.EnumColor;
import mekanism.client.jei.interfaces.IJEIIngredientHelper;
import mekanism.client.newgui.IGuiWrapper;
import mekanism.client.newgui.element.GuiElement;
import mekanism.client.newgui.element.slot.GuiSlot;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.MekanismLang;
import mekanism.common.inventory.ISlotClickHandler;
import mekanism.common.inventory.ISlotClickHandler.IScrollableSlot;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.text.TextUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static mekanism.client.gui.element.GuiUtils.blit;
import static mekanism.client.gui.element.GuiUtils.fill;

public class GuiSlotScroll extends GuiElement implements IJEIIngredientHelper {

    private static final ResourceLocation SLOTS = MekanismUtils.getResource(MekanismUtils.ResourceType.GUI_SLOT, "slots.png");
    private static final ResourceLocation SLOTS_DARK = MekanismUtils.getResource(MekanismUtils.ResourceType.GUI_SLOT, "slots_dark.png");
    private static final DecimalFormat COUNT_FORMAT = new DecimalFormat("#.#");

    static {
        COUNT_FORMAT.setRoundingMode(RoundingMode.FLOOR);
    }

    private final GuiScrollBar scrollBar;

    private final int xSlots, ySlots;
    private final Supplier<List<IScrollableSlot>> slotList;
    private final ISlotClickHandler clickHandler;

    public GuiSlotScroll(IGuiWrapper gui, int x, int y, int xSlots, int ySlots, Supplier<List<IScrollableSlot>> slotList, ISlotClickHandler clickHandler) {
        super(gui, x, y, xSlots * 18 + 18, ySlots * 18);
        this.xSlots = xSlots;
        this.ySlots = ySlots;
        this.slotList = slotList;
        this.clickHandler = clickHandler;
        scrollBar = addChild(new GuiScrollBar(gui, relativeX + xSlots * 18 + 4, y, ySlots * 18,
                () -> getSlotList() == null ? 0 : (int) Math.ceil((double) getSlotList().size() / xSlots), () -> ySlots));
    }

    @Override
    public void drawBackground(int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(mouseX, mouseY, partialTicks);
        minecraft.renderEngine.bindTexture(getSlotList() == null ? SLOTS_DARK : SLOTS);
        blit(x, y, 0, 0, xSlots * 18, ySlots * 18, 288, 288);

        List<IScrollableSlot> list = getSlotList();
        if (list != null) {
            int slotStart = scrollBar.getCurrentSelection() * xSlots, max = xSlots * ySlots;
            for (int i = 0; i < max; i++) {
                int slot = slotStart + i;
                // terminate if we've exceeded max slot pos
                if (slot >= list.size()) {
                    break;
                }
                int slotX = x + (i % xSlots) * 18, slotY = y + (i / xSlots) * 18;
                renderSlot(list.get(slot), slotX, slotY);
            }
        }
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        super.renderForeground(mouseX, mouseY);
        int xAxis = mouseX - getGuiLeft(), yAxis = mouseY - getGuiTop();
        int slotX = (xAxis - relativeX) / 18, slotY = (yAxis - relativeY) / 18;
        if (slotX >= 0 && slotY >= 0 && slotX < xSlots && slotY < ySlots) {
            int slotStartX = relativeX + slotX * 18 + 1, slotStartY = relativeY + slotY * 18 + 1;
            if (xAxis >= slotStartX && xAxis < slotStartX + 16 && yAxis >= slotStartY && yAxis < slotStartY + 16) {
                fill(slotStartX, slotStartY, slotStartX + 16, slotStartY + 16, GuiSlot.DEFAULT_HOVER_COLOR);
                MekanismRenderer.resetColor();
            }
        }
    }

    @Override
    public void renderToolTip(int mouseX, int mouseY) {
        super.renderToolTip(mouseX, mouseY);
        IScrollableSlot slot = getSlot(mouseX, mouseY, relativeX, relativeY);
        if (slot != null) {
            renderSlotTooltip(slot, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return scrollBar.adjustScroll(delta) || super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (gui().currentlyQuickCrafting()) {
            //If the player is currently quick crafting don't do any special handling for as if they clicked in the screen
            return super.mouseReleased(mouseX, mouseY, button);
        }
        super.mouseReleased(mouseX, mouseY, button);
        IScrollableSlot slot = getSlot(mouseX, mouseY, x, y);
        clickHandler.onClick(slot, button, GuiScreen.isShiftKeyDown(), minecraft.player.inventory.getItemStack());
        return true;
    }

    private IScrollableSlot getSlot(double mouseX, double mouseY, int relativeX, int relativeY) {
        List<IScrollableSlot> list = getSlotList();
        if (list == null) {
            return null;
        }
        int slotX = (int) ((mouseX - relativeX) / 18), slotY = (int) ((mouseY - relativeY) / 18);
        // terminate if we clicked the border of a slot
        int slotStartX = relativeX + slotX * 18 + 1, slotStartY = relativeY + slotY * 18 + 1;
        if (mouseX < slotStartX || mouseX >= slotStartX + 16 || mouseY < slotStartY || mouseY >= slotStartY + 16) {
            return null;
        }
        // terminate if we aren't looking at a slot on-screen
        if (slotX < 0 || slotY < 0 || slotX >= xSlots || slotY >= ySlots) {
            return null;
        }
        int slot = (slotY + scrollBar.getCurrentSelection()) * xSlots + slotX;
        // terminate if the slot doesn't exist
        if (slot >= list.size()) {
            return null;
        }
        return list.get(slot);
    }

    private void renderSlot(IScrollableSlot slot, int slotX, int slotY) {
        // sanity checks
        if (isSlotEmpty(slot)) {
            return;
        }
        gui().renderItemWithOverlay(slot.getItem().getStack(), slotX + 1, slotY + 1, 1, "");
        if (slot.getCount() > 1) {
            renderSlotText(getCountText(slot.getCount()), slotX + 1, slotY + 1);
        }
    }

    private void renderSlotTooltip(IScrollableSlot slot, int slotX, int slotY) {
        // sanity checks
        if (isSlotEmpty(slot)) {
            return;
        }
        ItemStack stack = slot.getItem().getStack();
        long count = slot.getCount();
        if (count < 10_000) {
            gui().renderItemTooltip(stack, slotX, slotY);
        } else {
            //If the slot's displayed count is truncated, make sure we also add the actual amount to the tooltip
            gui().renderItemTooltipWithExtra(stack, slotX, slotY, Collections.singletonList(MekanismLang.QIO_STORED_COUNT.translateColored(EnumColor.GREY, EnumColor.INDIGO, TextUtils.format(count)).getFormattedText()));
        }
    }

    private boolean isSlotEmpty(IScrollableSlot slot) {
        return slot.getItem() == null || slot.getItem().getStack().isEmpty();
    }

    private void renderSlotText(String text, int x, int y) {
        GlStateManager.pushMatrix();
        MekanismRenderer.resetColor();
        float scale = 0.6F;
        int width = getFont().getStringWidth(text);
        //If we need a lower scale due to having a lot of text, calculate it
        scale = Math.min(1, 16F / (width * scale)) * scale;
        float yAdd = 4 - (scale * 8) / 2F;
        GlStateManager.translate(x + 16 - width * scale, y + 9 + yAdd, 200F);
        GlStateManager.scale(scale, scale, scale);

        getFont().drawString(text, 0, 0, 0xFFFFFF);
        GlStateManager.popMatrix();
    }

    private String getCountText(long count) {
        //Note: For cases like 9,999,999 we intentionally display as 9999.9K instead of 10M so that people
        // do not think they have more stored than they actually have just because it is rounding up
        if (count <= 1) {
            return null;
        } else if (count < 10_000) {
            return Long.toString(count);
        } else if (count < 10_000_000) {
            return COUNT_FORMAT.format(count / 1_000D) + "K";
        } else if (count < 10_000_000_000L) {
            return COUNT_FORMAT.format(count / 1_000_000D) + "M";
        } else if (count < 10_000_000_000_000L) {
            return COUNT_FORMAT.format(count / 1_000_000_000D) + "B";
        }
        return ">10T";
    }

    private List<IScrollableSlot> getSlotList() {
        return slotList.get();
    }

    @Nullable
    @Override
    public Object getIngredient(double mouseX, double mouseY) {
        IScrollableSlot slot = getSlot(mouseX, mouseY, x, y);
        return slot == null ? null : slot.getItem().getStack();
    }
}
