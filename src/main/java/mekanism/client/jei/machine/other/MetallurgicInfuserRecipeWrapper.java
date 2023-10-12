package mekanism.client.jei.machine.other;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import mekanism.client.gui.element.GuiUtils;
import mekanism.client.jei.machine.MekanismRecipeWrapper;
import mekanism.common.InfuseStorage;
import mekanism.common.config.MekanismConfig;
import mekanism.common.recipe.machines.MetallurgicInfuserRecipe;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;

public class MetallurgicInfuserRecipeWrapper<RECIPE extends MetallurgicInfuserRecipe> extends MekanismRecipeWrapper<RECIPE> {

    public MetallurgicInfuserRecipeWrapper(RECIPE recipe) {
        super(recipe);
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        List<ItemStack> inputStacks = Collections.singletonList(recipe.recipeInput.inputStack);
        List<ItemStack> infuseStacks = MetallurgicInfuserRecipeCategory.getInfuseStacks(recipe.getInput().infuse.getType());
        ingredients.setInput(VanillaTypes.ITEM, recipe.recipeInput.inputStack);
        ingredients.setInputLists(VanillaTypes.ITEM, Arrays.asList(inputStacks, infuseStacks));
        ingredients.setOutput(VanillaTypes.ITEM, recipe.recipeOutput.output);
    }

    @Override
    public void drawInfo(Minecraft mc, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        if (mc.currentScreen != null) {
            mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GuiUtils.drawTiledSprite(2,2,52,4,52,recipe.getInput().infuse.getType().sprite,GuiUtils.TilingDirection.DOWN_RIGHT);
        }
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        if (mouseX >= 2 && mouseX < 6 && mouseY >= 2 && mouseY < 54) {
            InfuseStorage infuse = recipe.getInput().infuse;
            return Collections.singletonList(infuse.getType().getLocalizedName() + ": " + infuse.getAmount());
        } else if (mouseX >= 162 && mouseX < 166 && mouseY >= 6 && mouseY < 6 + 52) {
            return Collections.singletonList(LangUtils.localize("gui.using") + ":" + MekanismUtils.getEnergyDisplay(MekanismConfig.current().usage.metallurgicInfuser.val()) + "/t");
        }
        return Collections.emptyList();
    }
}
