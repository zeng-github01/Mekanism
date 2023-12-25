package mekanism.grs.common.integration.groovyscript.machinerecipe;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.ItemStackInput;
import mekanism.common.recipe.machines.CellSeparatorRecipe;
import mekanism.common.recipe.outputs.ChanceOutput;
import mekanism.grs.common.integration.groovyscript.GrSMekanismAdd;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CellSeparator extends VirtualizedMekanismRegistry<CellSeparatorRecipe> {

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public CellSeparator() {
        super(RecipeHandler.Recipe.CELL_SEPARATOR);
    }

    public CellSeparatorRecipe add(IIngredient ingredient, ItemStack output) {
        return add(ingredient, output, null, 0.0);
    }

    public CellSeparatorRecipe add(IIngredient ingredient, ItemStack output, ItemStack secondary) {
        return add(ingredient, output, secondary, 1.0);
    }

    public CellSeparatorRecipe add(IIngredient ingredient, ItemStack output, ItemStack secondary, double chance) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Cell Separator recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(IngredientHelper.isEmpty(output), () -> "output must not be empty");
        if (msg.postIfNotEmpty()) return null;

        boolean withSecondary = !IngredientHelper.isEmpty(secondary);
        if (withSecondary) {
            if (chance <= 0) chance = 1;
            secondary = secondary.copy();
        }

        output = output.copy();
        CellSeparatorRecipe recipe1 = null;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            CellSeparatorRecipe recipe;
            ChanceOutput chanceOutput = withSecondary ? new ChanceOutput(output, secondary, chance) : new ChanceOutput(output);
            recipe = new CellSeparatorRecipe(new ItemStackInput(itemStack.copy()), chanceOutput);
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
            CellSeparatorRecipe recipe = recipeRegistry.get().remove(new ItemStackInput(itemStack));
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

    public static class RecipeBuilder extends AbstractRecipeBuilder<CellSeparatorRecipe> {
        private ItemStack extra = ItemStack.EMPTY;
        private double chance = 1.0;

        public RecipeBuilder extra(ItemStack extra) {
            this.extra = extra;
            return this;
        }

        public RecipeBuilder chance(double chance) {
            this.chance = chance;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Cell Separator recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            msg.add(chance < 0 || chance > 1, "chance must be between 0 and 1.0, yet it was {}", chance);
        }

        @Override
        public @Nullable CellSeparatorRecipe register() {
            if (!validate()) return null;
            ChanceOutput chanceOutput = extra.isEmpty()
                    ? new ChanceOutput(output.get(0))
                    : new ChanceOutput(output.get(0), extra, chance);
            CellSeparatorRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                CellSeparatorRecipe r = new CellSeparatorRecipe(new ItemStackInput(itemStack.copy()), chanceOutput);
                if (recipe == null) recipe = r;
                GrSMekanismAdd.get().cellSeparator.add(r);
            }
            return recipe;
        }
    }
}
