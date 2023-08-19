package mekanism.client.gui.chemical;

import mekanism.client.gui.GuiAdvancedElectricMachine;
import mekanism.common.recipe.machines.InjectionRecipe;
import mekanism.common.tile.prefab.TileEntityAdvancedElectricMachine;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiChemicalInjectionChamber extends GuiAdvancedElectricMachine<InjectionRecipe> {

    public GuiChemicalInjectionChamber(InventoryPlayer inventory, TileEntityAdvancedElectricMachine<InjectionRecipe> tile) {
        super(inventory, tile);
    }

}
