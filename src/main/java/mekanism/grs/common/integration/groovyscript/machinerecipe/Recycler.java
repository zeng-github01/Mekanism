package mekanism.grs.common.integration.groovyscript.machinerecipe;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.machines.RecyclerRecipe;
import mekanism.common.recipe.outputs.ChanceOutput2;
import mekanism.grs.common.integration.groovyscript.GrSMekanismAdd;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class Recycler extends VirtualizedMekanismRegistry<RecyclerRecipe> {

    public Recycler() {
        super(RecipeHandler.Recipe.RECYCLER);
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public RecyclerRecipe add(IIngredient ingredient, ItemStack output) {
        return add(ingredient, output, 1.0);
    }

    public RecyclerRecipe add(IIngredient ingredient, ItemStack output, double chance) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Recycler recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(IngredientHelper.isEmpty(output), () -> "output must not be empty");
        if (msg.postIfNotEmpty()) return null;
        boolean withSecondary = !IngredientHelper.isEmpty(output);
        if (withSecondary) {
            if (chance <= 0) chance = 1;
            output = output.copy();
        }
        RecyclerRecipe recipe1 = null;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            RecyclerRecipe recipe;
            ChanceOutput2 chanceOutput = new ChanceOutput2(output, chance);
            recipe = new RecyclerRecipe(new ItemStackInput(itemStack.copy()), chanceOutput);
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
            RecyclerRecipe recipe = recipeRegistry.get().remove(new ItemStackInput(itemStack));
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

    public static class RecipeBuilder extends AbstractRecipeBuilder<RecyclerRecipe> {
        private double chance = 1.0;

        public RecipeBuilder chance(double chance) {
            this.chance = chance;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Recycler recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(chance < 0 || chance > 1, "chance must be between 0 and 1.0, yet it was {}", chance);
        }

        @Override
        public @Nullable RecyclerRecipe register() {
            if (!validate()) return null;
            ChanceOutput2 chanceOutput2 = new ChanceOutput2(output.get(0), chance);
            RecyclerRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                RecyclerRecipe r = new RecyclerRecipe(new ItemStackInput(itemStack.copy()), chanceOutput2);
                if (recipe == null) recipe = r;
                GrSMekanismAdd.get().recycler.add(r);
            }
            return recipe;
        }
    }
}
