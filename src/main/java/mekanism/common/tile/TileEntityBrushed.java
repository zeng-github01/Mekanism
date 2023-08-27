package mekanism.common.tile;

import java.util.Map;
import mekanism.common.block.states.BlockStateMachine.MachineType;
import mekanism.common.recipe.RecipeHandler.Recipe;
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.machines.BrushedRecipe;
import mekanism.common.tile.prefab.TileEntityElectricMachine;

public class TileEntityBrushed extends TileEntityElectricMachine<BrushedRecipe> {

    public TileEntityBrushed() {
        super("brushed", MachineType.BRUSHED, 200);
    }

    @Override
    public Map<ItemStackInput, BrushedRecipe> getRecipes() {
        return Recipe.BRUSHED.get();
    }
}
