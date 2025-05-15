package mekanism.client.gui.element;

import mekanism.client.gui.IGuiWrapper;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiSlot extends GuiElement {

    protected final int xLocation;
    protected final int yLocation;
    protected SlotOverlay overlay = null;
    protected final SlotType type;
    protected final ISlotInfoHandler handler;


    public GuiSlot(SlotType type, IGuiWrapper gui, ResourceLocation def, int x, int y) {
        this(type, gui, def, x, y, new ISlotInfoHandler() {
            @Override
            public boolean getSlotCanTip() {
                return false;
            }
        });
    }


    public GuiSlot(SlotType type, IGuiWrapper gui, ResourceLocation def, int x, int y, ISlotInfoHandler handler) {
        super(MekanismUtils.getResource(ResourceType.SLOT, "Slot_Icon.png"), gui, def);
        xLocation = x;
        yLocation = y;
        this.type = type;
        this.handler = handler;
    }

    public GuiSlot with(SlotOverlay overlay) {
        this.overlay = overlay;
        return this;
    }


    @Override
    public Rectangle4i getBounds(int guiWidth, int guiHeight) {
        return new Rectangle4i(guiWidth + xLocation, guiHeight + yLocation, type.width, type.height);
    }

    @Override
    public void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight) {
        mc.renderEngine.bindTexture(RESOURCE);
        guiObj.drawTexturedRect(guiWidth + xLocation, guiHeight + yLocation, type.textureX, type.textureY, type.width, type.height);
        if (overlay != null) {
            int w = overlay.width;
            int h = overlay.height;
            int xLocationOverlay = xLocation + (type.width - w) / 2;
            int yLocationOverlay = yLocation + (type.height - h) / 2;
            guiObj.drawTexturedRect(guiWidth + xLocationOverlay, guiHeight + yLocationOverlay, overlay.textureX, overlay.textureY, w, h);
        }
        mc.renderEngine.bindTexture(defaultLocation);
    }

    @Override
    protected boolean inBounds(int xAxis, int yAxis) {
        return xAxis >= xLocation && xAxis <= xLocation + type.width && yAxis >= yLocation && yAxis <= yLocation + type.height;
    }


    @Override
    public void renderForeground(int xAxis, int yAxis) {
        mc.renderEngine.bindTexture(RESOURCE);
        if (inBounds(xAxis, yAxis) && handler.getSlotCanTip()) {
            List<String> strings = new ArrayList<>();
            switch (type) {
                case POWER -> {
                    strings.add(LangUtils.localize("mekanism.gui.slot.power"));
                    strings.add(LangUtils.localize("mekanism.gui.slot.power.tooltip"));
                    displayTooltips(strings, xAxis, yAxis);
                }
                case INPUT -> {
                    strings.add(LangUtils.localize("mekanism.gui.slot.input"));
                    strings.add(LangUtils.localize("mekanism.gui.slot.input.tooltip"));
                    displayTooltips(strings, xAxis, yAxis);
                }
                case EXTRA -> {
                    strings.add(LangUtils.localize("mekanism.gui.slot.extra"));
                    strings.add(LangUtils.localize("mekanism.gui.slot.extra.tooltip"));
                    displayTooltips(strings, xAxis, yAxis);
                }
                case OUTPUT, OUTPUT_LARGE, OUTPUT_WIDE, OUTPUT_LARGE_WIDE -> {
                    strings.add(LangUtils.localize("mekanism.gui.slot.output"));
                    strings.add(LangUtils.localize("mekanism.gui.slot.output.tooltip"));
                    displayTooltips(strings, xAxis, yAxis);
                }
            }
        }

        mc.renderEngine.bindTexture(defaultLocation);
    }

    @Override
    public void preMouseClicked(int xAxis, int yAxis, int button) {
    }


    @Override
    public void mouseClicked(int xAxis, int yAxis, int button) {
    }

    public enum SlotType {
        NORMAL(18, 18, 0, 0),
        POWER(18, 18, 18, 0),
        INPUT(18, 18, 36, 0),
        EXTRA(18, 18, 54, 0),
        OUTPUT(18, 18, 72, 0),
        AQUA(18, 18, 36, 54),
        OUTPUT_LARGE(26, 26, 90, 0),
        NORMAL_LARGE(26, 26, 90, 26),
        OUTPUT_WIDE(42, 26, 116, 0),
        OUTPUT_LARGE_WIDE(36, 54, 116, 26),
        STATE_HOLDER(16, 16, 0, 72),
        WORD(18, 18, 72, 54);

        public final int width;
        public final int height;

        public final int textureX;
        public final int textureY;

        SlotType(int w, int h, int x, int y) {
            width = w;
            height = h;

            textureX = x;
            textureY = y;
        }
    }

    public enum SlotOverlay {
        MINUS(18, 18, 0, 18),
        PLUS(18, 18, 18, 18),
        POWER(18, 18, 36, 18),
        INPUT(18, 18, 54, 18),
        OUTPUT(18, 18, 72, 18),
        CHECK(18, 18, 0, 36),
        FORMULA(18, 18, 36, 36),
        UPGRADE(18, 18, 54, 36),
        MODULE(18, 18, 72, 36),
        WIND_OFF(12, 12, 0, 88),
        WIND_ON(12, 12, 12, 88),
        NO_SUN(12, 12, 24, 88),
        SEES_SUN(12, 12, 36, 88),
        SELECT(18, 18, 54, 54),
        ;

        public final int width;
        public final int height;

        public final int textureX;
        public final int textureY;

        SlotOverlay(int w, int h, int x, int y) {
            width = w;
            height = h;

            textureX = x;
            textureY = y;
        }
    }

    public abstract static class ISlotInfoHandler {

        public abstract boolean getSlotCanTip();
    }
}
