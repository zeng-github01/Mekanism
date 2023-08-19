package mekanism.client.gui;

import mekanism.client.gui.element.gauge.GuiGasGauge;
import mekanism.client.gui.element.gauge.GuiGauge.Type;
import mekanism.common.inventory.container.ContainerNull;
import mekanism.common.tile.TileEntityAmbientAccumulator;
import mekanism.common.util.LangUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAmbientAccumulator extends GuiMekanismTile<TileEntityAmbientAccumulator> {

    public GuiAmbientAccumulator(EntityPlayer player, TileEntityAmbientAccumulator tile) {
        super(tile, new ContainerNull(player, tile));
        addGuiElement(new GuiGasGauge(() -> tileEntity.collectedGas, Type.WIDE, this, getGuiLocation(), 26, 16));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(tileEntity.getName(), (xSize / 2) - (fontRenderer.getStringWidth(tileEntity.getName()) / 2), 4, 0x404040);
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

}
