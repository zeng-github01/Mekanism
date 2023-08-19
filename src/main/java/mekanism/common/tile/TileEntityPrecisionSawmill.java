package mekanism.common.tile;

import java.util.Map;
import mekanism.common.block.states.BlockStateMachine.MachineType;
import mekanism.common.recipe.RecipeHandler.Recipe;
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.machines.SawmillRecipe;

public class TileEntityPrecisionSawmill extends TileEntityChanceMachine<SawmillRecipe> {

    public TileEntityPrecisionSawmill() {
        super("sawmill", MachineType.PRECISION_SAWMILL, 200);
    }

    @Override
    public Map<ItemStackInput, SawmillRecipe> getRecipes() {
        return Recipe.PRECISION_SAWMILL.get();
    }
}
