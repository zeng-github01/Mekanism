package mekanism.client.gui;

import mekanism.client.gui.element.*;
import mekanism.client.gui.element.slot.GuiEnergySlot;
import mekanism.client.gui.element.slot.GuiInputSlot;
import mekanism.client.gui.element.slot.GuiOutputSlot;
import mekanism.common.inventory.container.ContainerModificationStation;
import mekanism.common.tile.TileEntityModificationStation;
import mekanism.common.util.LangUtils;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiModificationStation extends GuiMekanismTile<TileEntityModificationStation> implements IJeiNoShowRecipe{

    public GuiModificationStation(InventoryPlayer inventory, TileEntityModificationStation tile) {
        super(tile, new ContainerModificationStation(inventory, tile));
        ResourceLocation resource = getGuiLocation();
        addGuiElement(new GuiPowerBar(this, tileEntity, resource, 164, 15));
        addGuiElement(new GuiEnergyInfo(tileEntity, this, resource));
        addGuiElement(new GuiInputSlot( this, resource, 25, 33, tileEntity).with(GuiSlot.SlotOverlay.MODULE));
        addGuiElement(new GuiEnergySlot( this, resource, 140, 33,tileEntity));
        addGuiElement(new GuiOutputSlot( this, resource, 115, 33, tileEntity));
        addGuiElement(new GuiProgress(new GuiProgress.IProgressInfoHandler() {
            @Override
            public double getProgress() {
                return tileEntity.getScaledProgress();
            }
        }, GuiProgress.ProgressBar.LARGE_RIGHT, this, resource, 53, 37,true,false));
        addGuiElement(new GuiPlayerSlot(this, resource));
    }


    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(tileEntity.getName(), (xSize / 2) - (fontRenderer.getStringWidth(tileEntity.getName()) / 2), 4, 0x404040);
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }
}
