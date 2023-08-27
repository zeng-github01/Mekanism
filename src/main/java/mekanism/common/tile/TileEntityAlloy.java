package mekanism.common.tile;

import java.util.Map;
import mekanism.common.block.states.BlockStateMachine.MachineType;
import mekanism.common.recipe.RecipeHandler.Recipe;
import mekanism.common.recipe.inputs.DoubleMachineInput;
import mekanism.common.recipe.machines.AlloyRecipe;
import mekanism.common.tile.prefab.TileEntityDoubleElectricMachine;

public class TileEntityAlloy extends TileEntityDoubleElectricMachine<AlloyRecipe> {

    public TileEntityAlloy() {
        super("combiner", MachineType.ALLOY, 200);
    }

    @Override
    public Map<DoubleMachineInput, AlloyRecipe> getRecipes() {
        return Recipe.ALLOY.get();
    }
}
