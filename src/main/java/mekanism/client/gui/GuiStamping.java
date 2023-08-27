package mekanism.client.gui;

import mekanism.common.recipe.machines.StampingRecipe;
import mekanism.common.tile.prefab.TileEntityElectricMachine;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiStamping extends GuiElectricMachine<StampingRecipe> {

    public GuiStamping(InventoryPlayer inventory, TileEntityElectricMachine<StampingRecipe> tile) {
        super(inventory, tile);
    }
}
