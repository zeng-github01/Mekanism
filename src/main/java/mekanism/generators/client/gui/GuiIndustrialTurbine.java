package mekanism.generators.client.gui;

import mekanism.api.TileNetworkList;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.button.GuiDisableableButton;
import mekanism.client.gui.element.*;
import mekanism.client.gui.element.GuiRateBar.IRateInfoHandler;
import mekanism.client.gui.element.gauge.GuiGauge;
import mekanism.client.gui.element.gauge.GuiNumberGauge;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.config.MekanismConfig;
import mekanism.common.inventory.container.ContainerFilter;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.generators.client.gui.element.GuiTurbineTab;
import mekanism.generators.client.gui.element.GuiTurbineTab.TurbineTab;
import mekanism.generators.common.content.turbine.TurbineUpdateProtocol;
import mekanism.generators.common.tile.turbine.TileEntityTurbineCasing;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.Arrays;

@SideOnly(Side.CLIENT)
public class GuiIndustrialTurbine extends GuiMekanismTile<TileEntityTurbineCasing> {

    public GuiDisableableButton mode;

    public GuiIndustrialTurbine(InventoryPlayer inventory, TileEntityTurbineCasing tile) {
        super(tile, new ContainerFilter(inventory, tile));
        ResourceLocation resource = getGuiLocation();
        addGuiElement(new GuiTurbineTab(this, tileEntity, TurbineTab.STAT, resource));
        addGuiElement(new GuiPowerBar(this, tileEntity, resource, 164, 16));
        addGuiElement(new GuiRateBar(this, new IRateInfoHandler() {
            @Override
            public String getTooltip() {
                return LangUtils.localize("gui.steamInput") + ": " + (tileEntity.structure == null ? 0 : tileEntity.structure.lastSteamInput) + " mB/t";
            }

            @Override
            public double getLevel() {
                if (tileEntity.structure == null) {
                    return 0;
                }
                double rate = Math.min(tileEntity.structure.lowerVolume * tileEntity.structure.clientDispersers * MekanismConfig.current().generators.turbineDisperserGasFlow.val(),
                        tileEntity.structure.vents * MekanismConfig.current().generators.turbineVentGasFlow.val());
                if (rate == 0) {
                    return 0;
                }
                return (double) tileEntity.structure.lastSteamInput / rate;
            }
        }, resource, 40, 13));
        addGuiElement(new GuiEnergyInfo(() -> {
            double producing = tileEntity.structure == null ? 0 : tileEntity.structure.clientFlow * (MekanismConfig.current().general.maxEnergyPerSteam.val() / TurbineUpdateProtocol.MAX_BLADES) *
                    Math.min(tileEntity.structure.blades, tileEntity.structure.coils * MekanismConfig.current().generators.turbineBladesPerCoil.val());
            return Arrays.asList(LangUtils.localize("gui.storing") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getEnergy(), tileEntity.getMaxEnergy()),
                    LangUtils.localize("gui.producing") + ": " + MekanismUtils.getEnergyDisplay(producing) + "/t");
        }, this, resource));
        addGuiElement(new GuiInnerScreen(this, resource, 50, 23, 112, 41));
        addGuiElement(new GuiPlayerSlot(this, resource));
        addGuiElement(new GuiNumberGauge(new GuiNumberGauge.INumberInfoHandler() {

            @Override
            public TextureAtlasSprite getIcon() {
                return MekanismRenderer.getFluidTexture(tileEntity.structure != null ? tileEntity.structure.fluidStored : null, MekanismRenderer.FluidType.STILL);
            }

            @Override
            public double getLevel() {
                if (tileEntity.structure != null && tileEntity.structure.fluidStored != null) {
                    return tileEntity.structure.fluidStored.amount;
                } else {
                    return 0;
                }
            }

            @Override
            public double getMaxLevel() {
                if (tileEntity.structure != null && tileEntity.structure.fluidStored != null) {
                    return tileEntity.structure.getFluidCapacity();
                } else {
                    return 0;
                }
            }

            @Override
            public String getText(double level) {
                return tileEntity.structure != null ? (tileEntity.structure.fluidStored != null ? LangUtils.localizeFluidStack(tileEntity.structure.fluidStored) + ": " + tileEntity.structure.fluidStored.amount + "mB" : LangUtils.localize("gui.empty")) : "";
            }
        }, GuiGauge.Type.MEDIUM, this, resource, 6, 13));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 4, 0x404040);
        fontRenderer.drawString(tileEntity.getName(), (xSize / 2) - (fontRenderer.getStringWidth(tileEntity.getName()) / 2), 5, 0x404040);
        if (tileEntity.structure != null) {
            double energyMultiplier = (MekanismConfig.current().general.maxEnergyPerSteam.val() / TurbineUpdateProtocol.MAX_BLADES) *
                    Math.min(tileEntity.structure.blades, tileEntity.structure.coils * MekanismConfig.current().generators.turbineBladesPerCoil.val());
            double rate = tileEntity.structure.lowerVolume * (tileEntity.structure.clientDispersers * MekanismConfig.current().generators.turbineDisperserGasFlow.val());
            rate = Math.min(rate, tileEntity.structure.vents * MekanismConfig.current().generators.turbineVentGasFlow.val());
            renderScaledText(LangUtils.localize("gui.production") + ": " +
                    MekanismUtils.getEnergyDisplay(tileEntity.structure.clientFlow * energyMultiplier), 53, 26, 0x00CD00, 106);
            renderScaledText(LangUtils.localize("gui.flowRate") + ": " + tileEntity.structure.clientFlow + " mB/t", 53, 35, 0x00CD00, 106);
            renderScaledText(LangUtils.localize("gui.capacity") + ": " + tileEntity.structure.getFluidCapacity() + " mB", 53, 44, 0x00CD00, 106);
            renderScaledText(LangUtils.localize("gui.maxFlow") + ": " + rate + " mB/t", 53, 53, 0x00CD00, 106);
            String name = LangUtils.localize(tileEntity.structure.dumpMode.getLangKey());
            renderScaledText(name, 156 - (int) (fontRenderer.getStringWidth(name) * getNeededScale(name, 66)), 73, 0x404040, 66);
        }
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        buttonList.add(mode = new GuiDisableableButton(0, guiLeft + 159, guiTop + 72, 10, 10, () -> tileEntity.structure != null ? tileEntity.structure.dumpMode.ordinal() : 0).with(GuiDisableableButton.ImageOverlay.GAS_MOD));
    }


    @Override
    protected void actionPerformed(GuiButton guibutton) throws IOException {
        super.actionPerformed(guibutton);
        if (guibutton.id == mode.id) {
            Mekanism.packetHandler.sendToServer(new TileEntityMessage(tileEntity, TileNetworkList.withContents(0)));
            SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
        }
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "Null.png");
    }
}