package mekanism.client.jei.machine.other;

import mekanism.client.jei.machine.MekanismRecipeWrapper;
import mekanism.common.recipe.machines.ThermalEvaporationRecipe;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.UnitDisplayUtils;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import javax.annotation.Nonnull;

public class ThermalEvaporationRecipeWrapper<RECIPE extends ThermalEvaporationRecipe> extends MekanismRecipeWrapper<RECIPE> {

    public ThermalEvaporationRecipeWrapper(RECIPE recipe) {
        super(recipe);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.FLUID, recipe.getInput().ingredient);
        ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutput().output);
    }

    @Override
    public void drawInfo(@Nonnull Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        FontRenderer fontRendererObj = minecraft.fontRenderer;
        fontRendererObj.drawString(LangUtils.localize("gui.formed"), 48, 10, 0xFF3CFE9A);
        fontRendererObj.drawString(LangUtils.localize("gui.height") + ": " + 18, 48, 19, 0xFF3CFE9A);
        fontRendererObj.drawString(LangUtils.localize("gui.temp") + ": " + MekanismUtils.getTemperatureDisplay(300, UnitDisplayUtils.TemperatureUnit.KELVIN), 48, 28, 0xFF3CFE9A);
        fontRendererObj.drawString(LangUtils.localize("gui.production") + " : 0.0 mB/t", 48, 37, 0xFF3CFE9A);
    }
}
