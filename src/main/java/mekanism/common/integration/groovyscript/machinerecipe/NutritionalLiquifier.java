package mekanism.common.integration.groovyscript.machinerecipe;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.mekanism.Mekanism;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.GasRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import mekanism.api.gas.GasStack;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.machines.NutritionalRecipe;
import mekanism.common.integration.groovyscript.GrSMekanismAdd;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class NutritionalLiquifier extends VirtualizedMekanismRegistry<NutritionalRecipe> {

    public NutritionalLiquifier() {
        super(RecipeHandler.Recipe.NUTRITIONAL_LIQUIFIER);
    }
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public NutritionalRecipe add(IIngredient ingredient, GasStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Nutritional Liquifier recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(Mekanism.isEmpty(output), () -> "output must not be empty");
        if (msg.postIfNotEmpty()) return null;

        output = output.copy();
        NutritionalRecipe recipe1 = null;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            NutritionalRecipe recipe = new NutritionalRecipe(itemStack.copy(), output);
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
            NutritionalRecipe recipe = recipeRegistry.get().remove(new ItemStackInput(itemStack));
            if (recipe != null) {
                addBackup(recipe);
                found = true;
            }
        }
        if (!found) {
            removeError("could not find recipe for {}", ingredient);
        }
        return found;
    }

    public static class RecipeBuilder extends GasRecipeBuilder<NutritionalRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Nutritional Liquifier recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg);
            validateGases(msg, 0, 0, 1, 1);
        }

        @Override
        public @Nullable NutritionalRecipe  register() {
            if (!validate()) return null;
            NutritionalRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                NutritionalRecipe r = new NutritionalRecipe(itemStack.copy(),gasOutput.get(0));
                if (recipe == null)recipe =r;
                GrSMekanismAdd.get().nutritionalLiquifier.add(r);
            }
            return recipe;
        }
    }

}
