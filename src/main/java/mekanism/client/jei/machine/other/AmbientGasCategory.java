package mekanism.client.jei.machine.other;

import mekanism.api.gas.GasStack;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.gauge.GuiGasGauge;
import mekanism.client.gui.element.gauge.GuiGauge;
import mekanism.client.jei.BaseRecipeCategory;
import mekanism.client.jei.MekanismJEI;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.machines.AmbientGasRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;

public class AmbientGasCategory<WRAPPER extends AmbientGasRecipeWrapper<AmbientGasRecipe>> extends BaseRecipeCategory<WRAPPER> {

    public AmbientGasCategory(IGuiHelper helper) {
        super(helper, "mekanism:gui/Null.png", RecipeHandler.Recipe.AMBIENT_ACCUMULATOR.getJEICategory(),
                "tile.MachineBlock3.AmbientAccumulator.name", null, 6, 17, 103 + 17, 82 - 17);
    }

    @Override
    protected void addGuiElements() {
        guiElements.add(new GuiInnerScreen(this,guiLocation,7,18,80,60));
        guiElements.add(GuiGasGauge.getDummy(GuiGauge.Type.STANDARD, this, guiLocation, 103, 18).withColor(GuiGauge.TypeColor.ORANGE));
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, WRAPPER recipeWrapper, IIngredients ingredients) {
        AmbientGasRecipe tempRecipe = recipeWrapper.getRecipe();
        IGuiIngredientGroup<GasStack> gasStacks = recipeLayout.getIngredientsGroup(MekanismJEI.TYPE_GAS);
        initGas(gasStacks, 0, false, 104 - xOffset, 19 - yOffset, 16, 58, tempRecipe.recipeOutput.output, true);
    }
}
