package mekanism.client.gui;

import mekanism.api.Coord4D;
import mekanism.client.gui.button.GuiDisableableButton;
import mekanism.client.gui.element.*;
import mekanism.client.gui.element.slot.GuiEnergySlot;
import mekanism.client.gui.element.slot.GuiInputSlot;
import mekanism.client.gui.element.slot.GuiNormalSlot;
import mekanism.common.content.gear.Module;
import mekanism.common.inventory.container.ContainerModificationStation;
import mekanism.common.network.PacketRemoveModule.RemoveModuleMessage;
import mekanism.common.tile.TileEntityModificationStation;
import mekanism.common.util.LangUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class GuiModificationStation extends GuiMekanismTile<TileEntityModificationStation> implements IJeiNoShowRecipe{

    private GuiModuleScrollList moduleList;
    private GuiDisableableButton removeButton;
    private Module<?> selectedModule;

    public GuiModificationStation(InventoryPlayer inventory, TileEntityModificationStation tile) {
        super(tile, new ContainerModificationStation(inventory, tile));
        ySize += 64;
        ResourceLocation resource = getGuiLocation();
        addGuiElement(new GuiElementScreen(this, resource, 33, 19, 110, 74).isFrame());
        addGuiElement(moduleList = new GuiModuleScrollList(this, resource, 34, 20, 108, 74, () -> tileEntity.getStackInSlot(3).copy(), this::onModuleSelected));
        addGuiElement(new GuiPowerBar(this, tileEntity, resource, 154, 40));
        addGuiElement(new GuiEnergyInfo(tileEntity, this, resource));
        addGuiElement(new GuiInputSlot(this, resource, 34, 117, tileEntity).with(GuiSlot.SlotOverlay.MODULE));
        addGuiElement(new GuiEnergySlot(this, resource, 148, 20, tileEntity));
        addGuiElement(new GuiNormalSlot(this, resource, 124, 117));
        addGuiElement(new GuiProgress(new GuiProgress.IProgressInfoHandler() {
            @Override
            public double getProgress() {
                return tileEntity.getScaledProgress();
            }
        }, GuiProgress.ProgressBar.LARGE_RIGHT, this, resource, 65, 123, true, false));
        addGuiElement(new GuiPlayerSlot(this, resource, 7, 147));
        addGuiElement(new GuiPlayerArmmorSlot(this,resource,-26, 20, true));
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        buttonList.add(removeButton = new GuiDisableableButton(0, guiLeft + 34, guiTop + 96, 108, 17, LangUtils.localize("gui.remove"))
              .enabledTextColor(0xFF30C97A)
              .disabledTextColor(0xFF24965B)
              .hoveredtextColor(0xFF3CFE9A));
        removeButton.enabled = selectedModule != null;
    }

    @Override
    protected void actionPerformed(net.minecraft.client.gui.GuiButton guibutton) throws IOException {
        super.actionPerformed(guibutton);
        if (guibutton.id == 0 && selectedModule != null) {
            mekanism.common.Mekanism.packetHandler.sendToServer(new RemoveModuleMessage(Coord4D.get(tileEntity), selectedModule.getData(), GuiScreen.isShiftKeyDown()));
        }
    }

    private void onModuleSelected(Module<?> module) {
        selectedModule = module;
        if (removeButton != null) {
            removeButton.enabled = selectedModule != null;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(tileEntity.getName(), (xSize / 2) - (fontRenderer.getStringWidth(tileEntity.getName()) / 2), 4, 0x404040);
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
        if (removeButton != null && removeButton.visible && selectedModule != null) {
            int xAxis = mouseX - guiLeft;
            int yAxis = mouseY - guiTop;
            if (removeButton.isMouseOver()) {
                displayTooltip(LangUtils.localize("tooltip.remove_all_modules"), xAxis, yAxis);
            }
        }
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }
}
