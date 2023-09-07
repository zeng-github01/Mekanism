package mekanism.client.gui;

import mekanism.client.gui.element.GuiPlayerSlot;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.tab.GuiSecurityTab;
import mekanism.common.inventory.InventoryPersonalChest;
import mekanism.common.inventory.container.ContainerPersonalChest;
import mekanism.common.tile.TileEntityPersonalChest;
import mekanism.common.util.LangUtils;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPersonalChest extends GuiMekanismTile<TileEntityPersonalChest> {

    public GuiPersonalChest(InventoryPlayer inventory, TileEntityPersonalChest tile) {
        super(tile, new ContainerPersonalChest(inventory, tile));
        //xSize += 26;
        ySize += 64;
        addGuiElement(new GuiSecurityTab(this, tileEntity, getGuiLocation()));
        addGuiElement(new GuiPlayerSlot(this, getGuiLocation(), 7, 147));
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 9; x++) {
                addGuiElement(new GuiSlot(GuiSlot.SlotType.NORMAL, this, getGuiLocation(), 7 + x * 18, 25 + y * 18));
            }
        }
    }

    public GuiPersonalChest(InventoryPlayer inventory, InventoryPersonalChest inv) {
        super(null, new ContainerPersonalChest(inventory, inv));
        //xSize += 26;
        ySize += 64;
        addGuiElement(new GuiSecurityTab(this, getGuiLocation(), inv.currentHand));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String chest = LangUtils.localize("tile.MachineBlock.PersonalChest.name");
        fontRenderer.drawString(chest, (xSize / 2) - (fontRenderer.getStringWidth(chest) / 2), 6, 0x404040);
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

}
