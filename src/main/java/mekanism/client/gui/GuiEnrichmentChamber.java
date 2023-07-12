package mekanism.client.gui;

import mekanism.common.recipe.machines.EnrichmentRecipe;
import mekanism.common.tile.prefab.TileEntityElectricMachine;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEnrichmentChamber extends GuiElectricMachine<EnrichmentRecipe> {

    public GuiEnrichmentChamber(InventoryPlayer inventory, TileEntityElectricMachine<EnrichmentRecipe> tile) {
        super(inventory, tile);
    }

}