package mekanism.client.gui.robit;

import mekanism.client.gui.element.GuiProgress;
import mekanism.client.gui.element.GuiSlot;
import mekanism.common.entity.EntityRobit;
import mekanism.common.inventory.container.robit.ContainerRobitSmelting;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiRobitSmelting extends GuiRobit {

    public GuiRobitSmelting(InventoryPlayer inventory, EntityRobit entity) {
        super(entity, new ContainerRobitSmelting(inventory, entity));
        addGuiElement(new GuiSlot(GuiSlot.SlotType.NORMAL, this, getGuiLocation(), 55, 16));
        addGuiElement(new GuiSlot(GuiSlot.SlotType.NORMAL, this, getGuiLocation(), 55, 52));
        addGuiElement(new GuiSlot(GuiSlot.SlotType.NORMAL_LARGE, this, getGuiLocation(), 111, 30));
        addGuiElement(new GuiProgress(new GuiProgress.IProgressInfoHandler() {
            @Override
            public double getProgress() {
                return (double) robit.furnaceCookTime / 200;
            }
        }, GuiProgress.ProgressBar.TALL_RIGHT, this, getGuiLocation(), 78, 34));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(LangUtils.localize("gui.robit.smelting"), 8, 6, 0x404040);
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, ySize - 93, 0x404040);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected String getBackgroundImage() {
        return "Null.png";
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(int xAxis, int yAxis) {
        super.drawGuiContainerBackgroundLayer(xAxis, yAxis);
        mc.getTextureManager().bindTexture(MekanismUtils.getResource(MekanismUtils.ResourceType.PROGRESS, "Progress_Icon.png"));
        drawTexturedModalRect(guiLeft + 57, guiTop + 37, 1, 14, 14, 14);
        if (robit.furnaceBurnTime > 0) {
            int displayInt = robit.furnaceBurnTime * 13 / robit.currentItemBurnTime;
            drawTexturedModalRect(guiLeft + 56, guiTop + 37 + 12 - displayInt, 18, 26 - displayInt, 14, displayInt + 1);
        }
    }

    @Override
    protected boolean shouldOpenGui(int id) {
        return id != 3;
    }

}
