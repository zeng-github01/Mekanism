package mekanism.client.gui;

import mekanism.api.TileNetworkList;
import mekanism.api.gas.GasStack;
import mekanism.client.gui.element.GuiBlack;
import mekanism.client.gui.element.GuiBlackScreenframe;
import mekanism.client.gui.element.GuiRedstoneControl;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.SlotOverlay;
import mekanism.client.gui.element.GuiSlot.SlotType;
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.client.gui.element.tab.GuiSideConfigurationTab;
import mekanism.client.gui.element.tab.GuiTransporterConfigTab;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.inventory.container.ContainerGasTank;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.TileEntityGasTank;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiGasTank extends GuiMekanismTile<TileEntityGasTank> {

    public GuiGasTank(InventoryPlayer inventory, TileEntityGasTank tile) {
        super(tile, new ContainerGasTank(inventory, tile));
        ResourceLocation resource = getGuiLocation();
        addGuiElement(new GuiRedstoneControl(this, tileEntity, resource, 0, 0));
        addGuiElement(new GuiSecurityTab(this, tileEntity, resource, 0, 0));
        addGuiElement(new GuiSideConfigurationTab(this, tileEntity, resource));
        addGuiElement(new GuiTransporterConfigTab(this, 34, tileEntity, resource));
        addGuiElement(new GuiSlot(SlotType.INPUT, this, resource, 15, 16).with(SlotOverlay.PLUS));
        addGuiElement(new GuiSlot(SlotType.OUTPUT, this, resource, 15, 46).with(SlotOverlay.MINUS));
        addGuiElement(new GuiBlackScreenframe(GuiBlackScreenframe.BlackScreen.GASTANK, this, resource, 42, 37));
        addGuiElement(new GuiBlack(this, resource, 43, 38, 116, 25));

    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String stored = "" + (tileEntity.gasTank.getStored() == Integer.MAX_VALUE ? LangUtils.localize("gui.infinite") : tileEntity.gasTank.getStored());
        String capacityInfo = stored + " / " + (tileEntity.tier.getStorage() == Integer.MAX_VALUE ? LangUtils.localize("gui.infinite") : tileEntity.tier.getStorage());
        fontRenderer.drawString(tileEntity.getName(), (xSize / 2) - (fontRenderer.getStringWidth(tileEntity.getName()) / 2), 4, 0x404040);
        renderScaledText(LangUtils.localize("gui.gas") + ": " + (tileEntity.gasTank.getGas() != null ? tileEntity.gasTank.getGas().getGas().getLocalizedName()
                : LangUtils.localize("gui.none")), 45, 40, 0x33ff99, 112);
        fontRenderer.drawString(capacityInfo, 45, 49, 0x33ff99);
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, ySize - 96 + 2, 0x404040);
        String name = LangUtils.localize(tileEntity.dumping.getLangKey());
        fontRenderer.drawString(name, 156 - fontRenderer.getStringWidth(name), 73, 0x404040);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    protected boolean inBounds(int xAxis, int yAxis) {
        return xAxis >= 160 && xAxis <= 167 && yAxis >= 73 && yAxis <= 80;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(int xAxis, int yAxis) {
        super.drawGuiContainerBackgroundLayer(xAxis, yAxis);
        drawTexturedModalRect(guiLeft + 159, guiTop + 72, 9, 167, 10, 10);
        drawTexturedModalRect(guiLeft + 42, guiTop + 16, 0, 192, 118, 12);
        int displayInt = tileEntity.dumping.ordinal();
        drawTexturedModalRect(guiLeft + 160, guiTop + 73, 59 + 8 * displayInt, inBounds(xAxis, yAxis) ? 167 : 175, 8, 8);
        GasStack gas = tileEntity.gasTank.getGas();
        if (gas != null) {
            MekanismRenderer.color(gas);
            int scale = (int) (((double) tileEntity.gasTank.getStored() / tileEntity.tier.getStorage()) * 116);
            displayGauge(43, 17, scale, 10, gas.getGas().getSprite());
            MekanismRenderer.resetColor();
        }
    }

    public void displayGauge(int xPos, int yPos, int sizeX, int sizeY, TextureAtlasSprite icon) {
        if (icon != null) {
            mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            drawTexturedModalRect(guiLeft + xPos, guiTop + yPos, icon, sizeX, sizeY);
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException {
        super.mouseClicked(x, y, button);
        int xAxis = x - guiLeft;
        int yAxis = y - guiTop;
        if (xAxis > 160 && xAxis < 169 && yAxis > 73 && yAxis < 82) {
            TileNetworkList data = TileNetworkList.withContents(0);
            Mekanism.packetHandler.sendToServer(new TileEntityMessage(tileEntity, data));
            SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
        }
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, "GuiBlankIcon.png");
    }
}