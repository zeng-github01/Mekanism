package mekanism.common.recipe.serializer;

import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.api.recipes.MetallurgicInfuserRecipe;
import mekanism.common.recipe.ingredient.chemical.ChemicalIngredientDeserializer;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient.InfusionStackIngredient;

public class MetallurgicInfuserRecipeSerializer<RECIPE extends MetallurgicInfuserRecipe> extends
      ItemStackChemicalToItemStackRecipeSerializer<InfuseType, InfusionStack, InfusionStackIngredient, RECIPE> {

    public MetallurgicInfuserRecipeSerializer(IFactory<InfuseType, InfusionStack, InfusionStackIngredient, RECIPE> factory) {
        super(factory);
    }

    @Override
    protected ChemicalIngredientDeserializer<InfuseType, InfusionStack, InfusionStackIngredient> getDeserializer() {
        return ChemicalIngredientDeserializer.INFUSION;
    }
}