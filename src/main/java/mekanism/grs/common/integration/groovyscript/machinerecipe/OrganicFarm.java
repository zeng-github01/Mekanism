package mekanism.grs.common.integration.groovyscript.machinerecipe;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.mekanism.Mekanism;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.GasRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import mekanism.api.gas.GasStack;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.AdvancedMachineInput;
import mekanism.common.recipe.machines.FarmRecipe;
import mekanism.common.recipe.outputs.ChanceOutput;
import mekanism.grs.common.integration.groovyscript.GrSMekanismAdd;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class OrganicFarm extends VirtualizedMekanismRegistry<FarmRecipe> {

    public OrganicFarm() {
        super(RecipeHandler.Recipe.ORGANIC_FARM);
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public FarmRecipe add(IIngredient ingredient, GasStack gasInput, ItemStack output) {
        return add(ingredient, gasInput, output, null, 0.0);

    }

    public FarmRecipe add(IIngredient ingredient, GasStack gasInput, ItemStack output, ItemStack secondary) {
        return add(ingredient, gasInput, output, secondary, 1.0);
    }

    public FarmRecipe add(IIngredient ingredient, GasStack gasInput, ItemStack output, ItemStack secondary, double chance) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Organic Farm recipe").error();
        msg.add(IngredientHelper.isEmpty(ingredient), () -> "input must not be empty");
        msg.add(Mekanism.isEmpty(gasInput), () -> "input must not be empty");
        msg.add(IngredientHelper.isEmpty(output), () -> "output must not be empty");
        if (msg.postIfNotEmpty()) return null;
        boolean withSecondary = !IngredientHelper.isEmpty(secondary);
        if (withSecondary) {
            if (chance <= 0) chance = 1;
            secondary = secondary.copy();
        }
        output = output.copy();
        FarmRecipe recipe1 = null;
        for (ItemStack itemStack : ingredient.getMatchingStacks()) {
            FarmRecipe recipe;
            ChanceOutput chanceOutput = withSecondary ? new ChanceOutput(output, secondary, chance) : new ChanceOutput(output);
            recipe = new FarmRecipe(new AdvancedMachineInput(itemStack.copy(), gasInput.getGas()), chanceOutput);
            if (recipe1 == null) recipe1 = recipe;
            recipeRegistry.put(recipe);
            addScripted(recipe);
        }
        return recipe1;
    }

    public boolean removeByInput(IIngredient inputSolid, GasStack inputGas) {
        GroovyLog.Msg msg = GroovyLog.msg("Error removing Mekanism Organic Farm recipe").error();
        msg.add(IngredientHelper.isEmpty(inputSolid), () -> "input must not be empty");
        msg.add(Mekanism.isEmpty(inputGas), () -> "gas input must not be empty");
        if (msg.postIfNotEmpty()) return false;

        boolean found = false;
        for (ItemStack itemStack : inputSolid.getMatchingStacks()) {
            FarmRecipe recipe = recipeRegistry.get().remove(new AdvancedMachineInput(itemStack, inputGas.getGas()));
            if (recipe != null) {
                addBackup(recipe);
                found = true;
            }
        }
        if (!found) {
            removeError("could not find recipe for {} and {}", inputSolid, inputGas);
        }
        return found;
    }

    public static class RecipeBuilder extends GasRecipeBuilder<FarmRecipe> {

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
            return "Error adding Mekanism Organic Farm recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 1, 1);
            validateFluids(msg);
            validateGases(msg, 1, 1, 0, 0);
            msg.add(chance < 0 || chance > 1, "chance must be between 0 and 1.0, yet it was {}", chance);
        }

        @Override
        public @Nullable FarmRecipe register() {
            if (!validate()) return null;
            ChanceOutput chanceOutput = extra.isEmpty()
                    ? new ChanceOutput(output.get(0))
                    : new ChanceOutput(output.get(0), extra, chance);
            FarmRecipe recipe = null;
            for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                FarmRecipe r = new FarmRecipe(new AdvancedMachineInput(itemStack.copy(), gasInput.get(0).getGas()), chanceOutput);
                if (recipe == null) recipe = r;
                GrSMekanismAdd.get().organicFarm.add(r);
            }
            return recipe;
        }
    }

}
