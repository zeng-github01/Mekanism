package mekanism.client.gui;

import mekanism.common.recipe.machines.SmeltingRecipe;
import mekanism.common.tile.prefab.TileEntityElectricMachine;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEnergizedSmelter extends GuiElectricMachine<SmeltingRecipe> {

    public GuiEnergizedSmelter(InventoryPlayer inventory, TileEntityElectricMachine<SmeltingRecipe> tile) {
        super(inventory, tile);
    }

}
