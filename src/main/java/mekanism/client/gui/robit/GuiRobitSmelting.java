package mekanism.client.gui.robit;

import mekanism.client.gui.element.GuiPlayerSlot;
import mekanism.client.gui.element.GuiSlot;
import mekanism.common.entity.EntityRobit;
import mekanism.common.inventory.container.robit.ContainerRobitSmelting;
import mekanism.common.util.LangUtils;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiRobitSmelting extends GuiRobit {

    public GuiRobitSmelting(InventoryPlayer inventory, EntityRobit entity) {
        super(entity, new ContainerRobitSmelting(inventory, entity));
        addGuiElement(new GuiSlot(GuiSlot.SlotType.NORMAL,this,getGuiLocation(),55,16));
        addGuiElement(new GuiSlot(GuiSlot.SlotType.NORMAL,this,getGuiLocation(),55,52));
        addGuiElement(new GuiSlot(GuiSlot.SlotType.NORMAL_LARGE,this,getGuiLocation(),111,30));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(LangUtils.localize("gui.robit.smelting"), 8, 6, 0x404040);
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, ySize - 93, 0x404040);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected String getBackgroundImage() {
        return "GuiRoBitIcon.png";
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(int xAxis, int yAxis) {
        super.drawGuiContainerBackgroundLayer(xAxis, yAxis);
        drawTexturedModalRect(guiLeft + 80, guiTop + 35, 201, 0, 22, 15);
        drawTexturedModalRect(guiLeft + 57, guiTop + 37, 239, 17, 14, 14);
        int displayInt;
        if (robit.furnaceBurnTime > 0) {
            displayInt = getBurnTimeRemainingScaled(12);
            drawTexturedModalRect(guiLeft + 56, guiTop + 36 + 12 - displayInt, 224, 17 + 12 - displayInt, 14, displayInt + 2);
        }
        displayInt = getCookProgressScaled(24);
        drawTexturedModalRect(guiLeft + 79, guiTop + 34, 224, 0, displayInt + 1, 16);
    }

    @Override
    protected boolean shouldOpenGui(int id) {
        return id != 3;
    }

    private int getCookProgressScaled(int i) {
        return robit.furnaceCookTime * i / 200;
    }

    private int getBurnTimeRemainingScaled(int i) {
        if (robit.currentItemBurnTime == 0) {
            robit.currentItemBurnTime = 200;
        }
        return robit.furnaceBurnTime * i / robit.currentItemBurnTime;
    }
}