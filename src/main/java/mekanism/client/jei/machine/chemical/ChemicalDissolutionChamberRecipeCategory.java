package mekanism.client.jei.machine.chemical;

import mekanism.api.gas.GasStack;
import mekanism.client.gui.element.GuiProgress;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.gauge.GuiGasGauge;
import mekanism.client.gui.element.gauge.GuiGauge;
import mekanism.client.gui.element.slot.GuiEnergySlot;
import mekanism.client.gui.element.slot.GuiExtraSlot;
import mekanism.client.gui.element.slot.GuiInputSlot;
import mekanism.client.gui.element.slot.GuiOutputSlot;
import mekanism.client.jei.BaseRecipeCategory;
import mekanism.client.jei.MekanismJEI;
import mekanism.common.MekanismFluids;
import mekanism.common.recipe.RecipeHandler.Recipe;
import mekanism.common.recipe.machines.DissolutionRecipe;
import mekanism.common.tile.machine.TileEntityChemicalDissolutionChamber;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;

public class ChemicalDissolutionChamberRecipeCategory<WRAPPER extends ChemicalDissolutionChamberRecipeWrapper<DissolutionRecipe>> extends BaseRecipeCategory<WRAPPER> {

    public ChemicalDissolutionChamberRecipeCategory(IGuiHelper helper) {
        super(helper, "mekanism:gui/Null.png",
                Recipe.CHEMICAL_DISSOLUTION_CHAMBER.getJEICategory(), "gui.chemicalDissolutionChamber.short", GuiProgress.ProgressBar.LARGE_RIGHT, 4, 4, 169, 78);
    }

    @Override
    protected void addGuiElements() {
        guiElements.add(GuiGasGauge.getDummy(GuiGauge.Type.STANDARD, this, guiLocation, 7, 4).withColor(GuiGauge.TypeColor.RED));
        guiElements.add(GuiGasGauge.getDummy(GuiGauge.Type.STANDARD, this, guiLocation, 131, 13).withColor(GuiGauge.TypeColor.BLUE));
        guiElements.add(new GuiEnergySlot(this, guiLocation, 151, 13));
        guiElements.add(new GuiInputSlot( this, guiLocation, 27, 35));
        guiElements.add(new GuiOutputSlot( this, guiLocation, 151, 54).with(GuiSlot.SlotOverlay.PLUS));
        guiElements.add(new GuiExtraSlot(this, guiLocation, 7, 64).with(GuiSlot.SlotOverlay.MINUS));
        guiElements.add(new GuiProgress(new GuiProgress.IProgressInfoHandler() {
            @Override
            public double getProgress() {
                return (float) timer.getValue() / 20F;
            }
        }, progressBar, this, guiLocation, 62, 39,false));
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, WRAPPER recipeWrapper, IIngredients ingredients) {
        DissolutionRecipe tempRecipe = recipeWrapper.getRecipe();
        IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
        itemStacks.init(0, true, 27 - xOffset, 35 - yOffset);
        itemStacks.set(0, tempRecipe.getInput().ingredient);
        IGuiIngredientGroup<GasStack> gasStacks = recipeLayout.getIngredientsGroup(MekanismJEI.TYPE_GAS);
        initGas(gasStacks, 0, true, 8 - xOffset, 5 - yOffset, 16, 58,
                new GasStack(MekanismFluids.SulfuricAcid, TileEntityChemicalDissolutionChamber.BASE_INJECT_USAGE * 100),
                true);
        initGas(gasStacks, 1, false, 132 - xOffset, 14 - yOffset, 16, 58, tempRecipe.getOutput().output, true);
    }
}
