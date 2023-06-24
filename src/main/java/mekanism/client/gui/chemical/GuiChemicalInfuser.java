package mekanism.client.gui.chemical;

import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.*;
import mekanism.client.gui.element.GuiProgress.IProgressInfoHandler;
import mekanism.client.gui.element.GuiProgress.ProgressBar;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.gui.element.gauge.GuiGasGauge;
import mekanism.client.gui.element.gauge.GuiGauge;
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.client.gui.element.tab.GuiUpgradeTab;
import mekanism.common.inventory.container.ContainerChemicalInfuser;
import mekanism.common.tile.TileEntityChemicalInfuser;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiChemicalInfuser extends GuiMekanismTile<TileEntityChemicalInfuser> {

    public GuiChemicalInfuser(InventoryPlayer inventory, TileEntityChemicalInfuser tile) {
        super(tile, new ContainerChemicalInfuser(inventory, tile));
        ResourceLocation resource = getGuiLocation();
        addGuiElement(new GuiSecurityTab(this, tileEntity, resource, 0, 0));
        addGuiElement(new GuiRedstoneControl(this, tileEntity, resource, 0, 0));
        addGuiElement(new GuiUpgradeTab(this, tileEntity, resource, 0, 0));
        addGuiElement(new GuiPowerBarHorizontal(this, tileEntity, resource, 115 - 2, 74));
        addGuiElement(new GuiEnergyInfo(() -> {
            String usage = MekanismUtils.getEnergyDisplay(tileEntity.clientEnergyUsed);
            return Arrays.asList(LangUtils.localize("gui.using") + ": " + usage + "/t",
                    LangUtils.localize("gui.needed") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxEnergy() - tileEntity.getEnergy()));
        }, this, resource));
        addGuiElement(new GuiGasGauge(() -> tileEntity.leftTank, GuiGauge.Type.STANDARD, this, resource, 25, 13));
        addGuiElement(new GuiGasGauge(() -> tileEntity.centerTank, GuiGauge.Type.STANDARD, this, resource, 79, 4));
        addGuiElement(new GuiGasGauge(() -> tileEntity.rightTank, GuiGauge.Type.STANDARD, this, resource, 133, 13));
        addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 154, 4).with(SlotOverlay.POWER));
        addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 154, 55).with(SlotOverlay.MINUS));
        addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 4, 55).with(SlotOverlay.MINUS));
        addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 79, 64).with(SlotOverlay.PLUS));
        addGuiElement(new GuiProgress(new IProgressInfoHandler() {
            @Override
            public double getProgress() {
                return tileEntity.getActive() ? 1 : 0;
            }
        }, ProgressBar.SMALL_RIGHT, this, resource, 45, 38));
        addGuiElement(new GuiProgress(new IProgressInfoHandler() {
            @Override
            public double getProgress() {
                return tileEntity.getActive() ? 1 : 0;
            }
        }, ProgressBar.SMALL_LEFT, this, resource, 99, 38));
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "GuiBlankIcon.png");
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(tileEntity.getName(), (xSize / 2) - (fontRenderer.getStringWidth(tileEntity.getName()) / 2), 4, 0x404040);
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 4, 0x404040);
        int xAxis = mouseX - guiLeft;
        int yAxis = mouseY - guiTop;
        if (xAxis >= -21 && xAxis <= -3 && yAxis >= 116 && yAxis <= 134) {
            List<String> info = new ArrayList<>();
            boolean energy = tileEntity.getEnergy() < tileEntity.energyPerTick || tileEntity.getEnergy() == 0;
            boolean output = tileEntity.centerTank.getStored() == tileEntity.centerTank.getMaxGas();
            if (energy) {
                info.add(LangUtils.localize("gui.no_energy"));
            }
            if (output) {
                info.add(LangUtils.localize("gui.gas_no_space"));
            }
            if (output || energy) {
                displayTooltips(info, xAxis, yAxis);
            }
        }
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(int xAxis, int yAxis) {
        super.drawGuiContainerBackgroundLayer(xAxis, yAxis);
        boolean output = tileEntity.centerTank.getStored() == tileEntity.centerTank.getMaxGas();
        boolean energy = tileEntity.getEnergy() < tileEntity.energyPerTick || tileEntity.getEnergy() == 0;
        if (output) {
            mc.getTextureManager().bindTexture(MekanismUtils.getResource(ResourceType.GUI_ELEMENT, "Warning.png"));
            drawTexturedModalRect(guiLeft + 79 + 9, guiTop + 4 + 1, 9, 1, 8, 29);
            drawTexturedModalRect(guiLeft + 79 + 9, guiTop + 4 + 31, 9, 32, 8, 28);
        }
        if (output || energy) {
            mc.getTextureManager().bindTexture(MekanismUtils.getResource(ResourceType.GUI_ELEMENT, "GuiWarningInfo.png"));
            drawTexturedModalRect(guiLeft - 26, guiTop + 112, 0, 0, 26, 26);
            addGuiElement(new GuiWarningInfo(this, getGuiLocation(), false));
        }
    }
}