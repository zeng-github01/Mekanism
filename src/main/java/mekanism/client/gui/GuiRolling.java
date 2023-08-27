package mekanism.client.gui;

import mekanism.common.recipe.machines.RollingRecipe;
import mekanism.common.tile.prefab.TileEntityElectricMachine;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiRolling extends GuiElectricMachine<RollingRecipe> {

    public GuiRolling(InventoryPlayer inventory, TileEntityElectricMachine<RollingRecipe> tile) {
        super(inventory, tile);
    }

}
