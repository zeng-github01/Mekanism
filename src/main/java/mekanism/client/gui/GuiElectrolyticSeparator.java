package mekanism.client.gui;

import mekanism.api.TileNetworkList;
import mekanism.client.gui.element.*;
import mekanism.client.gui.element.GuiProgress.IProgressInfoHandler;
import mekanism.client.gui.element.GuiProgress.ProgressBar;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.client.gui.element.gauge.GuiGasGauge;
import mekanism.client.gui.element.gauge.GuiGauge;
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.client.gui.element.tab.GuiSideConfigurationTab;
import mekanism.client.gui.element.tab.GuiTransporterConfigTab;
import mekanism.client.gui.element.tab.GuiUpgradeTab;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.inventory.container.ContainerElectrolyticSeparator;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.TileEntityElectrolyticSeparator;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiElectrolyticSeparator extends GuiMekanismTile<TileEntityElectrolyticSeparator> {

    public GuiElectrolyticSeparator(InventoryPlayer inventory, TileEntityElectrolyticSeparator tile) {
        super(tile, new ContainerElectrolyticSeparator(inventory, tile));
        ResourceLocation resource = getGuiLocation();
        addGuiElement(new GuiRedstoneControl(this, tileEntity, resource));
        addGuiElement(new GuiUpgradeTab(this, tileEntity, resource));
        addGuiElement(new GuiSideConfigurationTab(this, tileEntity, resource));
        addGuiElement(new GuiTransporterConfigTab(this, 34, tileEntity, resource));
        addGuiElement(new GuiEnergyInfo(() -> {
            String usage = MekanismUtils.getEnergyDisplay(tileEntity.clientEnergyUsed);
            return Arrays.asList(LangUtils.localize("gui.using") + ": " + usage + "/t",
                    LangUtils.localize("gui.needed") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxEnergy() - tileEntity.getEnergy()));
        }, this, resource));
        addGuiElement(new GuiFluidGauge(() -> tileEntity.fluidTank, GuiGauge.Type.STANDARD, this, resource, 5, 10));
        addGuiElement(new GuiGasGauge(() -> tileEntity.leftTank, GuiGauge.Type.SMALL, this, resource, 58, 18));
        addGuiElement(new GuiGasGauge(() -> tileEntity.rightTank, GuiGauge.Type.SMALL, this, resource, 100, 18));
        addGuiElement(new GuiPowerBar(this, tileEntity, resource, 164, 15));
        addGuiElement(new GuiSecurityTab(this, tileEntity, resource));
        addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 25, 34));
        addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 58, 51));
        addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 100, 51));
        addGuiElement(new GuiSlot(SlotType.NORMAL, this, resource, 142, 34).with(SlotOverlay.POWER));
        addGuiElement(new GuiProgress(new IProgressInfoHandler() {
            @Override
            public double getProgress() {
                return tileEntity.getActive() ? 1 : 0;
            }
        }, ProgressBar.BI, this, resource, 78, 29));
        addGuiElement(new GuiPlayerSlot(this,resource));
    }

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException {
        super.mouseClicked(x, y, button);
        int xAxis = x - guiLeft;
        int yAxis = y - guiTop;
        if (xAxis > 8 && xAxis < 17 && yAxis > 73 && yAxis < 82) {
            TileNetworkList data = TileNetworkList.withContents((byte) 0);
            Mekanism.packetHandler.sendToServer(new TileEntityMessage(tileEntity, data));
            SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
        } else if (xAxis > 160 && xAxis < 169 && yAxis > 73 && yAxis < 82) {
            TileNetworkList data = TileNetworkList.withContents((byte) 1);
            Mekanism.packetHandler.sendToServer(new TileEntityMessage(tileEntity, data));
            SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
        }
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "Null.png");
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(tileEntity.getName(), (xSize / 2) - (fontRenderer.getStringWidth(tileEntity.getName()) / 2), 4, 0x404040);
        String name = LangUtils.localize(tileEntity.dumpLeft.getLangKey());
        renderScaledText(name, 21, 73, 0x404040, 66);
        name = LangUtils.localize(tileEntity.dumpRight.getLangKey());
        renderScaledText(name, 156 - (int) (fontRenderer.getStringWidth(name) * getNeededScale(name, 66)), 73, 0x404040, 66);

        int xAxis = mouseX - guiLeft;
        int yAxis = mouseY - guiTop;
        if (xAxis >= -21 && xAxis <= -3 && yAxis >= 116 && yAxis <= 134) {
            List<String> info = new ArrayList<>();
            boolean energy = tileEntity.getEnergy() < tileEntity.energyPerTick || tileEntity.getEnergy() == 0;
            boolean outLeft = tileEntity.leftTank.getStored() == tileEntity.leftTank.getMaxGas();
            boolean outRight = tileEntity.rightTank.getStored() == tileEntity.rightTank.getMaxGas();
            if (energy) {
                info.add(LangUtils.localize("gui.no_energy"));
            }
            if (outLeft) {
                info.add(LangUtils.localize("gui.gas_no_space"));
            }
            if (outRight) {
                info.add(LangUtils.localize("gui.gas_no_space"));
            }
            if (outLeft || energy || outRight) {
                displayTooltips(info, xAxis, yAxis);
            }
        }

        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    protected boolean inBounds(int xAxis, int yAxis) {
        return xAxis >= 8 && xAxis <= 15 && yAxis >= 73 && yAxis <= 80;
    }

    protected boolean inBounds2(int xAxis, int yAxis) {
        return xAxis >= 160 && xAxis <= 167 && yAxis >= 73 && yAxis <= 80;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(int xAxis, int yAxis) {
        super.drawGuiContainerBackgroundLayer(xAxis, yAxis);

        drawTexturedModalRect(guiLeft + 7, guiTop + 72, 9, 167, 10, 10);
        drawTexturedModalRect(guiLeft + 159, guiTop + 72, 9, 167, 10, 10);
        //Left
        int dumpLeft = tileEntity.dumpLeft.ordinal();
        drawTexturedModalRect(guiLeft + 8, guiTop + 73, 59 + 8 * dumpLeft, inBounds(xAxis, yAxis) ? 167 : 175, 8, 8);
        //Right
        int dumpRight = tileEntity.dumpRight.ordinal();
        drawTexturedModalRect(guiLeft + 160, guiTop + 73, 59 + 8 * dumpRight, inBounds2(xAxis, yAxis) ? 167 : 175, 8, 8);

        boolean energy = tileEntity.getEnergy() < tileEntity.energyPerTick || tileEntity.getEnergy() == 0;
        boolean outLeft = tileEntity.leftTank.getStored() == tileEntity.leftTank.getMaxGas();
        boolean outRight = tileEntity.rightTank.getStored() == tileEntity.rightTank.getMaxGas();
        if (outLeft) {
            mc.getTextureManager().bindTexture(MekanismUtils.getResource(MekanismUtils.ResourceType.GUI_ELEMENT, "Warning.png"));
            drawTexturedModalRect(guiLeft + 58 + 10, guiTop + 18 + 1, 0, 0, 7, 28);
        }
        if (outRight) {
            mc.getTextureManager().bindTexture(MekanismUtils.getResource(MekanismUtils.ResourceType.GUI_ELEMENT, "Warning.png"));
            drawTexturedModalRect(guiLeft + 100 + 10, guiTop + 18 + 1, 0, 0, 7, 28);
        }
        if (energy || outLeft || outRight) {
            mc.getTextureManager().bindTexture(MekanismUtils.getResource(MekanismUtils.ResourceType.TAB, "Warning_Info.png"));
            drawTexturedModalRect(guiLeft - 26, guiTop + 112, 0, 0, 26, 26);
            addGuiElement(new GuiWarningInfo(this, getGuiLocation(), false));
        }


    }
}