package mekanism.common.tile.machine;

import mekanism.common.block.states.BlockStateMachine.MachineType;
import mekanism.common.recipe.RecipeHandler.Recipe;
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.machines.RecyclerRecipe;
import mekanism.common.tile.prefab.TileEntityChanceMachine2;

import java.util.Map;

//TODO：修改这个配方类型，如果有新的配方，则使用新配方，否则使用默认配方
public class TileEntityRecycler extends TileEntityChanceMachine2<RecyclerRecipe> {

    public TileEntityRecycler() {
        super("Recycler", MachineType.RECYCLER, 200);
    }

    @Override
    public Map<ItemStackInput, RecyclerRecipe> getRecipes() {
        return Recipe.RECYCLER.get();
    }



}
