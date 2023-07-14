package mekanism.client.jei.machine.chemical;

import mekanism.api.gas.GasStack;
import mekanism.client.gui.element.GuiBlack;
import mekanism.client.gui.element.GuiProgress;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.gauge.GuiGasGauge;
import mekanism.client.gui.element.gauge.GuiGauge;
import mekanism.client.jei.BaseRecipeCategory;
import mekanism.client.jei.MekanismJEI;
import mekanism.common.recipe.RecipeHandler.Recipe;
import mekanism.common.recipe.machines.CrystallizerRecipe;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;

public class ChemicalCrystallizerRecipeCategory<WRAPPER extends ChemicalCrystallizerRecipeWrapper<CrystallizerRecipe>> extends BaseRecipeCategory<WRAPPER> {

    public ChemicalCrystallizerRecipeCategory(IGuiHelper helper) {
        super(helper, "mekanism:gui/GuiBlankIcon.png", Recipe.CHEMICAL_CRYSTALLIZER.getJEICategory(),
                "tile.MachineBlock2.ChemicalCrystallizer.name", GuiProgress.ProgressBar.LARGE_RIGHT, 5, 3, 147, 79);
    }

    @Override
    protected void addGuiElements() {
        guiElements.add(GuiGasGauge.getDummy(GuiGauge.Type.STANDARD, this, guiLocation, 5, 4));
        guiElements.add(new GuiSlot(GuiSlot.SlotType.EXTRA, this, guiLocation, 5, 64).with(GuiSlot.SlotOverlay.PLUS));
        guiElements.add(new GuiSlot(GuiSlot.SlotType.OUTPUT, this, guiLocation, 130, 56));
        guiElements.add(new GuiProgress(new GuiProgress.IProgressInfoHandler() {
            @Override
            public double getProgress() {
                return (float) timer.getValue() / 20F;
            }
        }, progressBar, this, guiLocation, 51, 60));
        guiElements.add(new GuiBlack(this, guiLocation, 27, 13, 121, 42).with(true));
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, WRAPPER recipeWrapper, IIngredients ingredients) {
        CrystallizerRecipe tempRecipe = recipeWrapper.getRecipe();
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        itemStacks.init(0, false, 130 - xOffset, 56 - yOffset);
        itemStacks.set(0, tempRecipe.getOutput().output);
        IGuiIngredientGroup<GasStack> gasStacks = recipeLayout.getIngredientsGroup(MekanismJEI.TYPE_GAS);
        initGas(gasStacks, 0, true, 6 - xOffset, 5 - yOffset, 16, 58, tempRecipe.getInput().ingredient, true);
    }
}