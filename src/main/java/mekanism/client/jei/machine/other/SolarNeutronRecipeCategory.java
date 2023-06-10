package mekanism.client.jei.machine.other;

import mekanism.api.gas.GasStack;
import mekanism.client.gui.element.GuiProgress;
import mekanism.client.gui.element.GuiProgress.*;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.GuiSlot.*;
import mekanism.client.gui.element.gauge.GuiGasGauge;
import mekanism.client.gui.element.gauge.GuiGauge.*;
import mekanism.client.jei.BaseRecipeCategory;
import mekanism.client.jei.MekanismJEI;
import mekanism.common.recipe.RecipeHandler.Recipe;
import mekanism.common.recipe.machines.SolarNeutronRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;

public class SolarNeutronRecipeCategory<WRAPPER extends SolarNeutronRecipeWrapper<SolarNeutronRecipe>> extends BaseRecipeCategory<WRAPPER> {

    public SolarNeutronRecipeCategory(IGuiHelper helper) {
        super(helper, "mekanism:gui/GuiBlank.png", Recipe.SOLAR_NEUTRON_ACTIVATOR.getJEICategory(),
                "tile.MachineBlock3.SolarNeutronActivator.name", ProgressBar.LARGE_RIGHT, 3, 12, 170, 62);
    }

    @Override
    protected void addGuiElements() {
        guiElements.add(GuiGasGauge.getDummy(Type.STANDARD, this, guiLocation, 25, 13));
        guiElements.add(GuiGasGauge.getDummy(Type.STANDARD, this, guiLocation, 133, 13));
        guiElements.add(new GuiSlot(SlotType.NORMAL, this, guiLocation, 4, 55).with(SlotOverlay.MINUS));
        guiElements.add(new GuiSlot(SlotType.NORMAL, this, guiLocation, 154, 55).with(SlotOverlay.PLUS));
        guiElements.add(new GuiProgress(new IProgressInfoHandler() {
            @Override
            public double getProgress() {
                return (float) timer.getValue() / 20F;
            }
        }, progressBar, this, guiLocation, 62, 38));
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, WRAPPER recipeWrapper, IIngredients ingredients) {
        SolarNeutronRecipe tempRecipe = recipeWrapper.getRecipe();
        IGuiIngredientGroup<GasStack> gasStacks = recipeLayout.getIngredientsGroup(MekanismJEI.TYPE_GAS);
        initGas(gasStacks, 0, true, 26 - xOffset, 14 - yOffset, 16, 58, tempRecipe.recipeInput.ingredient, true);
        initGas(gasStacks, 1, false, 134 - xOffset, 14 - yOffset, 16, 58, tempRecipe.recipeOutput.output, true);
    }
}