package mekanism.client.gui;

import mekanism.common.recipe.machines.AlloyRecipe;
import mekanism.common.tile.prefab.TileEntityDoubleElectricMachine;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAlloy extends GuiDoubleElectricMachine<AlloyRecipe> {

    public GuiAlloy(InventoryPlayer inventory, TileEntityDoubleElectricMachine<AlloyRecipe> tile) {
        super(inventory, tile);
    }
}
