package mekanism.common.integration.groovyscript.machinerecipe;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.compat.mods.mekanism.Mekanism;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.GasRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.Alias;
import mekanism.api.gas.GasStack;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.GasInput;
import mekanism.common.recipe.machines.IsotopicRecipe;
import mekanism.common.integration.groovyscript.GrSMekanismAdd;
import org.jetbrains.annotations.Nullable;

public class IsotopicCentrifuge extends VirtualizedMekanismRegistry<IsotopicRecipe> {

    public IsotopicCentrifuge() {
        super(RecipeHandler.Recipe.ISOTOPIC_CENTRIFUGE, Alias.generateOf("IsotopicCentrifuge").and(new String[]{"Isotopic_Centrifuge"}));
    }

    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    public IsotopicRecipe add(GasStack input, GasStack output) {
        GroovyLog.Msg msg = GroovyLog.msg("Error adding Mekanism Isotopic Centrifuge recipe").error();
        msg.add(Mekanism.isEmpty(input), () -> "input must not be empty");
        msg.add(Mekanism.isEmpty(output), () -> "output must not be empty");
        if (msg.postIfNotEmpty()) return null;

        IsotopicRecipe recipe = new IsotopicRecipe(input.copy(), output.copy());
        recipeRegistry.put(recipe);
        addScripted(recipe);
        return recipe;
    }

    public boolean removeByInput(GasStack input) {
        GroovyLog.Msg msg = GroovyLog.msg("Error removing Mekanism Isotopic Centrifuge recipe").error();
        msg.add(Mekanism.isEmpty(input), () -> "input must not be empty");
        if (msg.postIfNotEmpty()) return false;

        IsotopicRecipe recipe = recipeRegistry.get().remove(new GasInput(input));
        if (recipe != null) {
            addBackup(recipe);
            return true;
        }
        removeError("could not find recipe for %", input);
        return false;
    }

    public static class RecipeBuilder extends GasRecipeBuilder<IsotopicRecipe> {
        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Isotopic Centrifuge recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg);
            validateGases(msg, 1, 1, 1, 1);
        }

        @Override
        public @Nullable IsotopicRecipe register() {
            if (!validate()) return null;
            IsotopicRecipe recipe = new IsotopicRecipe(gasInput.get(0), gasOutput.get(0));
            GrSMekanismAdd.get().isotopicCentrifuge.add(recipe);
            return recipe;
        }
    }
}
