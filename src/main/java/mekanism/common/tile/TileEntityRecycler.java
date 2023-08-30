package mekanism.common.tile;

import java.util.Map;
import mekanism.common.block.states.BlockStateMachine.MachineType;
import mekanism.common.recipe.RecipeHandler.Recipe;
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.machines.RecyclerRecipe;
import mekanism.common.tile.prefab.TileEntityChanceMachine2;

public class TileEntityRecycler extends TileEntityChanceMachine2<RecyclerRecipe> {

    public TileEntityRecycler() {
        super("Recycler", MachineType.RECYCLER, 200);
    }

    @Override
    public Map<ItemStackInput, RecyclerRecipe> getRecipes() {
        return Recipe.RECYCLER.get();
    }
}
