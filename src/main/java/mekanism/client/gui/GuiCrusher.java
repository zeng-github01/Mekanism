package mekanism.client.gui;

import mekanism.common.recipe.machines.CrusherRecipe;
import mekanism.common.tile.prefab.TileEntityElectricMachine;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCrusher extends GuiElectricMachine<CrusherRecipe> {

    public GuiCrusher(InventoryPlayer inventory, TileEntityElectricMachine<CrusherRecipe> tile) {
        super(inventory, tile);
    }

}
