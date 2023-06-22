package mekanism.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mekanism.api.TileNetworkList;
import mekanism.client.gui.button.GuiButtonDisableableImage;
import mekanism.client.gui.element.GuiEnergyInfo;
import mekanism.client.gui.element.GuiPowerBar;
import mekanism.client.gui.element.GuiProgress;
import mekanism.client.gui.element.GuiProgress.IProgressInfoHandler;
import mekanism.client.gui.element.GuiProgress.ProgressBar;
import mekanism.client.gui.element.GuiRedstoneControl;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.client.gui.element.tab.GuiSideConfigurationTab;
import mekanism.client.gui.element.tab.GuiTransporterConfigTab;
import mekanism.client.gui.element.tab.GuiUpgradeTab;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.inventory.container.ContainerMetallurgicInfuser;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.TileEntityMetallurgicInfuser;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMetallurgicInfuser extends GuiMekanismTile<TileEntityMetallurgicInfuser> {

    private GuiButton toggleButton;

    public GuiMetallurgicInfuser(InventoryPlayer inventory, TileEntityMetallurgicInfuser tile) {
        super(tile, new ContainerMetallurgicInfuser(inventory, tile));
        ResourceLocation resource = getGuiLocation();
        addGuiElement(new GuiRedstoneControl(this, tileEntity, resource, 0, 0));
        addGuiElement(new GuiUpgradeTab(this, tileEntity, resource, 0, 0));
        addGuiElement(new GuiSecurityTab(this, tileEntity, resource, 0, 0));
        addGuiElement(new GuiSideConfigurationTab(this, tileEntity, resource));
        addGuiElement(new GuiTransporterConfigTab(this, 34, tileEntity, resource));
        addGuiElement(new GuiPowerBar(this, tileEntity, resource, 164, 15));
        addGuiElement(new GuiEnergyInfo(() -> {
            String multiplier = MekanismUtils.getEnergyDisplay(tileEntity.energyPerTick);
            return Arrays.asList(LangUtils.localize("gui.using") + ": " + multiplier + "/t",
                  LangUtils.localize("gui.needed") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxEnergy() - tileEntity.getEnergy()));
        }, this, resource));
        addGuiElement(new GuiSlot(SlotType.EXTRA, this, resource, 16, 34));
        addGuiElement(new GuiSlot(SlotType.INPUT, this, resource, 50, 42));
        addGuiElement(new GuiSlot(SlotType.POWER, this, resource, 142, 34).with(SlotOverlay.POWER));
        addGuiElement(new GuiSlot(SlotType.OUTPUT, this, resource, 108, 42));
        addGuiElement(new GuiProgress(new IProgressInfoHandler() {
            @Override
            public double getProgress() {
                return tileEntity.getScaledProgress();
            }
        }, ProgressBar.MEDIUM, this, resource, 70, 46));
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        buttonList.add(toggleButton = new GuiButtonDisableableImage(0, guiLeft + 147, guiTop + 72, 21, 10, 37, 177, -10, getGuiLocation()));
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) throws IOException {
        super.actionPerformed(guibutton);
        if (guibutton.id == toggleButton.id) {
            Mekanism.packetHandler.sendToServer(new TileEntityMessage(tileEntity, TileNetworkList.withContents(0)));
            SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
        }
    }


    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(tileEntity.getName(), 45, 6, 0x404040);
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
        int xAxis = mouseX - guiLeft;
        int yAxis = mouseY - guiTop;
        if (toggleButton.isMouseOver()) {
            displayTooltip(LangUtils.localize("gui.remove"), xAxis, yAxis);
        } else if (xAxis >= 7 && xAxis <= 11 && yAxis >= 17 && yAxis <= 69) {
            displayTooltip(tileEntity.infuseStored.getType() != null ? tileEntity.infuseStored.getType().getLocalizedName() + ": " + tileEntity.infuseStored.getAmount()
                                                                     : LangUtils.localize("gui.empty"), xAxis, yAxis);
        }else if (xAxis >= -21 && xAxis <= -3 && yAxis >= 116 && yAxis <= 134) {
            List<String> info = new ArrayList<>();
            boolean energy = tileEntity.getEnergy() < tileEntity.energyPerTick || tileEntity.getEnergy() == 0;
            boolean input = (tileEntity.infuseStored.getAmount() == 0) && (tileEntity.inventory.get(2).getCount() != 0);
            boolean outslot = tileEntity.inventory.get(3).getCount() == tileEntity.inventory.get(3).getMaxStackSize();
            if (energy){
                info.add(LangUtils.localize("gui.no_energy"));
            }
            if (input) {
                info.add(LangUtils.localize("gui.infuse_no_item"));
            }if (outslot){
                info.add(LangUtils.localize("gui.item_no_space"));
            }
            if (input || energy || outslot){
                displayTooltips(info, xAxis, yAxis);
            }
        }

        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(int xAxis, int yAxis) {
        super.drawGuiContainerBackgroundLayer(xAxis, yAxis);
        drawTexturedModalRect(guiLeft + 6,guiTop + 16,177,0,6,54);
        if (tileEntity.infuseStored.getType() != null) {
            mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            int displayInt = tileEntity.getScaledInfuseLevel(52);
            drawTexturedRectFromIcon(guiLeft + 7, guiTop + 17 + 52 - displayInt, tileEntity.infuseStored.getType().sprite, 4, displayInt);
        }

        boolean input = (tileEntity.infuseStored.getAmount() == 0) && (tileEntity.inventory.get(2).getCount() != 0);
        boolean energy = tileEntity.getEnergy() < tileEntity.energyPerTick || tileEntity.getEnergy() == 0;
        boolean outslot = tileEntity.inventory.get(3).getCount() == tileEntity.inventory.get(3).getMaxStackSize();
        if (outslot) {
            mc.getTextureManager().bindTexture(MekanismUtils.getResource(MekanismUtils.ResourceType.GUI_ELEMENT, "GuiSlot.png"));
            drawTexturedModalRect(guiLeft + 108, guiTop + 42,158,0,18,18);
        }if (input){
            mc.getTextureManager().bindTexture(MekanismUtils.getResource(MekanismUtils.ResourceType.GUI_ELEMENT, "Warning_Background.png"));
            drawTexturedModalRect(guiLeft + 6 + 1, guiTop + 16 + 1,0,0,4,52);
        }
        if (outslot || input || energy){
            mc.getTextureManager().bindTexture(MekanismUtils.getResource(MekanismUtils.ResourceType.GUI_ELEMENT, "GuiWarningInfo.png"));
            drawTexturedModalRect(guiLeft - 26, guiTop + 112,0,0,26,26);
        }
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "GuiBlankIcon.png");
    }
}