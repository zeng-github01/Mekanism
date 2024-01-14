package mekanism.common.integration.groovyscript.machinerecipe;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import mekanism.common.integration.groovyscript.GrSMekanismAdd;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.machines.StampingRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class Stamping extends VirtualizedMekanismRegistry<StampingRecipe> {
    public Stamping() {
        super(RecipeHandler.Recipe.STAMPING);
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public StampingRecipe add(IIngredient ingredient, ItemStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Stamping recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(IngredientHelper.isEmpty(output), () -> "output must not be empty");
        if (msg.postIfNotEmpty()) return null;

        output = output.copy();
        StampingRecipe recipe1 = null;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            StampingRecipe recipe = new StampingRecipe(itemStack.copy(), output);
            if (recipe1 == null) recipe1 = recipe;
            recipeRegistry.put(recipe);
            addScripted(recipe);
        }
        return recipe1;
    }

    public boolean removeByInput(IIngredient ingredient) {
        if (IngredientHelper.isEmpty(ingredient)) {
            removeError("input must not be empty");
            return false;
        }
        boolean found = false;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            StampingRecipe recipe = recipeRegistry.get().remove(new ItemStackInput(itemStack));
            if (recipe != null) {
                addBackup(recipe);
                found = true;
            }
        }
        if (!found) {
            removeError("could not find recipe for %s", ingredient);
        }
        return found;
    }

    public static class RecipeBuilder extends AbstractRecipeBuilder<StampingRecipe> {
        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Stamping recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
        }

        @Override
        public @Nullable StampingRecipe register() {
            if (!validate()) return null;
            StampingRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                StampingRecipe r = new StampingRecipe(itemStack.copy(), output.get(0));
                if (recipe == null) recipe = r;
                GrSMekanismAdd.get().stamping.add(r);
            }
            return recipe;
        }
    }
}
