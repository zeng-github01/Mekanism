package mekanism.generators.client.gui;

import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.element.GuiEnergyInfo;
import mekanism.client.gui.element.GuiPowerBar;
import mekanism.client.gui.element.GuiRedstoneControl;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.common.base.IRedstoneControl;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.generators.common.inventory.container.ContainerSolarGenerator;
import mekanism.generators.common.tile.TileEntitySolarGenerator;
import mekanism.generators.common.util.MekanismGeneratorUtils;
import mekanism.generators.common.util.MekanismGeneratorUtils.ResourceType;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;

@SideOnly(Side.CLIENT)
public class GuiSolarGenerator extends GuiMekanismTile<TileEntitySolarGenerator> {

    public GuiSolarGenerator(InventoryPlayer inventory, TileEntitySolarGenerator tile) {
        super(tile, new ContainerSolarGenerator(inventory, tile));
        ResourceLocation resource = getGuiLocation();
        addGuiElement(new GuiRedstoneControl(this, tileEntity, resource, 0, 0));
        addGuiElement(new GuiSecurityTab(this, tileEntity, resource, 0, 0));
        addGuiElement(new GuiEnergyInfo(Collections::emptyList, this, resource));
        addGuiElement(new GuiSlot(SlotType.POWER, this, resource, 142, 34).with(SlotOverlay.POWER));
        addGuiElement(new GuiPowerBar(this, tileEntity, resource, 164, 15));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(tileEntity.getName(), (xSize / 2) - (fontRenderer.getStringWidth(tileEntity.getName()) / 2), 4, 0x404040);
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 2, 0x404040);

        if (!tileEntity.getActive()) {
            String info = "";
            String info2 = "";
            String info3 = "";
            if (!tileEntity.canSeeSun()) {
                info = "gui.none";
                info3 = " ";
                info2 = "gui.solarGenerator.sun";
            }
            if (tileEntity.controlType == IRedstoneControl.RedstoneControl.HIGH && !tileEntity.redstone && tileEntity.canSeeSun()) {
                info = "control.high.desc";
            }
            if (tileEntity.controlType == IRedstoneControl.RedstoneControl.LOW && tileEntity.redstone && tileEntity.canSeeSun())
                info = "control.low.desc";
            if (tileEntity.getEnergy() == tileEntity.getMaxEnergy()) {
                info = "gui.enough_no_space";
            }
            renderScaledText(LangUtils.localize(info) + info3 + LangUtils.localize(info2), 49, 42, 0x00CD00, 78);
        } else {
            renderCenteredText(48, 80, 28, 0x00CD00, LangUtils.localize("gui.producing"));
            renderCenteredText(48, 80, 42, 0x00CD00, MekanismUtils.getEnergyDisplay(tileEntity.getProduction()) + "/t");
        }
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(int xAxis, int yAxis) {
        super.drawGuiContainerBackgroundLayer(xAxis, yAxis);
        drawTexturedModalRect(guiLeft + 20, guiTop + 37, 176, tileEntity.canSeeSun() ? 52 : 64, 12, 12);
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismGeneratorUtils.getResource(ResourceType.GUI, "GuiSolarGenerator.png");
    }
}