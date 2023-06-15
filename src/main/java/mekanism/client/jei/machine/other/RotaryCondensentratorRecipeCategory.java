package mekanism.client.jei.machine.other;

import mekanism.api.gas.GasStack;
import mekanism.client.gui.element.GuiProgress;
import mekanism.client.gui.element.GuiSlot;
import mekanism.client.gui.element.gauge.GuiFluidGauge;
import mekanism.client.gui.element.gauge.GuiGasGauge;
import mekanism.client.gui.element.gauge.GuiGauge;
import mekanism.client.jei.BaseRecipeCategory;
import mekanism.client.jei.MekanismJEI;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiIngredientGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;

public class RotaryCondensentratorRecipeCategory extends BaseRecipeCategory<RotaryCondensentratorRecipeWrapper> {

    private final boolean condensentrating;

    public RotaryCondensentratorRecipeCategory(IGuiHelper helper, boolean condensentrating) {
        super(helper, "mekanism:gui/GuiBlankIcon.png",
              condensentrating ? "mekanism.rotary_condensentrator_condensentrating" : "mekanism.rotary_condensentrator_decondensentrating",
              condensentrating ? "gui.condensentrating" : "gui.decondensentrating", null, 3, 12, 170, 71);
        this.condensentrating = condensentrating;
    }

    @Override
    protected void addGuiElements() {
        guiElements.add(GuiFluidGauge.getDummy(GuiGauge.Type.STANDARD, this, guiLocation, 133, 13));
        guiElements.add(GuiGasGauge.getDummy(GuiGauge.Type.STANDARD, this, guiLocation, 25, 13));
        guiElements.add(new GuiSlot(GuiSlot.SlotType.NORMAL, this, guiLocation, 4, 24).with(GuiSlot.SlotOverlay.PLUS));
        guiElements.add(new GuiSlot(GuiSlot.SlotType.NORMAL, this, guiLocation, 4, 55).with(GuiSlot.SlotOverlay.MINUS));
        guiElements.add(new GuiSlot(GuiSlot.SlotType.NORMAL, this, guiLocation, 154, 24));
        guiElements.add(new GuiSlot(GuiSlot.SlotType.NORMAL, this, guiLocation, 154, 55));
    }

    @Override
    public void drawExtras(Minecraft minecraft) {
        super.drawExtras(minecraft);
        guiElements.add(new GuiProgress(new GuiProgress.IProgressInfoHandler() {
                    @Override
                    public double getProgress() {
                        return 1F;
                    }
                }, condensentrating ? GuiProgress.ProgressBar.LARGE_RIGHT : GuiProgress.ProgressBar.LARGE_LEFT, this, guiLocation, 62, 38)
        );
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, RotaryCondensentratorRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiFluidStackGroup fluidStacks = recipeLayout.getFluidStacks();
        IGuiIngredientGroup<GasStack> gasStacks = recipeLayout.getIngredientsGroup(MekanismJEI.TYPE_GAS);
        fluidStacks.init(0, !condensentrating, 134 - xOffset, 14 - yOffset, 16, 58, RotaryCondensentratorRecipeWrapper.FLUID_AMOUNT, false, fluidOverlayLarge);
        if (condensentrating) {
            initGas(gasStacks, 0, true, 26 - xOffset, 14 - yOffset, 16, 58, new GasStack(recipeWrapper.getGasType(), RotaryCondensentratorRecipeWrapper.GAS_AMOUNT), true);
            fluidStacks.set(0, ingredients.getOutputs(VanillaTypes.FLUID).get(0));
        } else {
            initGas(gasStacks, 0, false, 26 - xOffset, 14 - yOffset, 16, 58, new GasStack(recipeWrapper.getGasType(), RotaryCondensentratorRecipeWrapper.GAS_AMOUNT), true);
            fluidStacks.set(0, ingredients.getInputs(VanillaTypes.FLUID).get(0));
        }
    }
}