package mekanism.client.gui;

import mekanism.common.recipe.machines.CombinerRecipe;
import mekanism.common.tile.prefab.TileEntityDoubleElectricMachine;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCombiner extends GuiDoubleElectricMachine<CombinerRecipe> {

    public GuiCombiner(InventoryPlayer inventory, TileEntityDoubleElectricMachine<CombinerRecipe> tile) {
        super(inventory, tile);
    }

}
