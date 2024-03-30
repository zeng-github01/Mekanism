package mekanism.common.integration.groovyscript.machinerecipe;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.compat.mods.mekanism.Mekanism;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.GasRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.ingredient.IngredientHelper;
import mekanism.api.gas.GasStack;
import mekanism.common.integration.groovyscript.GrSMekanismAdd;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.NucleosynthesizerInput;
import mekanism.common.recipe.machines.NucleosynthesizerRecipe;
import mekanism.common.recipe.outputs.ItemStackOutput;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;


public class AntiprotonicNucleosynthesizer extends VirtualizedMekanismRegistry<NucleosynthesizerRecipe> {

    public AntiprotonicNucleosynthesizer() {
        super(RecipeHandler.Recipe.ANTIPROTONIC_NUCLEOSYNTHESIZER, Alias.generateOfClass(AntiprotonicNucleosynthesizer.class).and("AntiprotonicNucleosynthesizer", "antiprotonic_nucleosynthesizer"));
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }


    public NucleosynthesizerRecipe add(IIngredient inputSolid, GasStack inputGas, ItemStack outputSolid, double energy, int duration) {
        NucleosynthesizerRecipe r = null;
        for (ItemStack item : inputSolid.getMatchingStacks()) {
            NucleosynthesizerRecipe recipe = new NucleosynthesizerRecipe(item, inputGas.copy(), outputSolid.copy(), energy, duration);
            if (r == null) r = recipe;
            recipeRegistry.put(recipe);
            addScripted(recipe);
        }
        return r;
    }

    public boolean removeByInput(IIngredient inputSolid, GasStack inputGas) {
        if (GroovyLog.msg("Error removing Mekanism Antiprotonic Nucleosynthesizer recipe").error()
                .add(IngredientHelper.isEmpty(inputSolid), () -> "item input must not be empty")
                .add(Mekanism.isEmpty(inputGas), () -> "input gas must not be empty")
                .error()
                .postIfNotEmpty()) {
            return false;
        }
        boolean found = false;
        for (ItemStack itemStack : inputSolid.getMatchingStacks()) {
            NucleosynthesizerRecipe recipe = recipeRegistry.get().remove(new NucleosynthesizerInput(itemStack, inputGas));
            if (recipe != null) {
                addBackup(recipe);
                found = true;
            }
        }
        if (!found) {
            removeError("could not find recipe for %s and %s", inputSolid, inputGas);
        }
        return found;
    }


    public static class RecipeBuilder extends GasRecipeBuilder<NucleosynthesizerRecipe> {
        private int duration;
        private double energy;

        public RecipeBuilder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public RecipeBuilder energy(double energy) {
            this.energy = energy;
            return this;
        }


        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Antiprotonic Nucleosynthesizer recipe";
        }


        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 1, 1, 1);
            validateFluids(msg);
            validateGases(msg, 1, 1, 0, 0);
            if (duration <= 0) duration = 100;
            if (energy <= 0) energy = 8000;
        }


        @Override
        public @Nullable NucleosynthesizerRecipe register() {
            if (!validate()) return null;
            ItemStackOutput itemStackOutput = new ItemStackOutput(output.getOrEmpty(0));
            NucleosynthesizerRecipe recipe = null;
            if (input.isEmpty()) {
                recipe = new NucleosynthesizerRecipe(new NucleosynthesizerInput(ItemStack.EMPTY, gasInput.get(0)), itemStackOutput, energy, duration);
                GrSMekanismAdd.get().antiprotonicNucleosynthesizer.add(recipe);
            } else {
                for (ItemStack itemStack : input.get(0).getMatchingStacks()) {
                    NucleosynthesizerRecipe r = new NucleosynthesizerRecipe(new NucleosynthesizerInput(itemStack.copy(), gasInput.get(0)), itemStackOutput, energy, duration);
                    if (recipe == null) recipe = r;
                    GrSMekanismAdd.get().antiprotonicNucleosynthesizer.add(r);
                }
            }
            return recipe;
        }
    }

}
