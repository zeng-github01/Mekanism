package mekanism.client.gui;

import mekanism.common.recipe.machines.TurningRecipe;
import mekanism.common.tile.prefab.TileEntityElectricMachine;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTurning extends GuiElectricMachine<TurningRecipe> {

    public GuiTurning(InventoryPlayer inventory, TileEntityElectricMachine<TurningRecipe> tile) {
        super(inventory, tile);
    }
}
