package mekanism.common.recipe.lookup;

import javax.annotation.Nullable;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.common.recipe.lookup.IRecipeLookupHandler.IRecipeTypedLookupHandler;
import mekanism.common.recipe.lookup.cache.InputRecipeCache.ItemFluidChemical;
import mekanism.common.recipe.lookup.cache.TripleInputRecipeCache;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.TriPredicate;
import net.minecraftforge.fluids.FluidStack;

/**
 * Helper expansion of {@link IRecipeLookupHandler} for easily implementing contains and find recipe lookups for recipes that takes three inputs.
 */
public interface ITripleRecipeLookupHandler<INPUT_A, INPUT_B, INPUT_C, RECIPE extends MekanismRecipe & TriPredicate<INPUT_A, INPUT_B, INPUT_C>,
      INPUT_CACHE extends TripleInputRecipeCache<INPUT_A, ?, INPUT_B, ?, INPUT_C, ?, RECIPE, ?, ?, ?>> extends IRecipeTypedLookupHandler<RECIPE, INPUT_CACHE> {

    /**
     * Checks if there is a matching recipe of type {@link #getRecipeType()} that has the given inputs.
     *
     * @param inputA Recipe input a.
     * @param inputB Recipe input b.
     * @param inputC Recipe input c.
     *
     * @return {@code true} if there is a match, {@code false} if there isn't.
     *
     * @apiNote See {@link TripleInputRecipeCache#containsInputABC(Level, Object, Object, Object)}, {@link TripleInputRecipeCache#containsInputBAC(Level, Object, Object,
     * Object)}, and {@link TripleInputRecipeCache#containsInputCAB(Level, Object, Object, Object)} for more details about when this method should be called versus when
     * {@link #containsRecipeBAC(Object, Object, Object)} or {@link #containsRecipeCAB(Object, Object, Object)} should be called.
     */
    default boolean containsRecipeABC(INPUT_A inputA, INPUT_B inputB, INPUT_C inputC) {
        return getRecipeType().getInputCache().containsInputABC(getHandlerWorld(), inputA, inputB, inputC);
    }

    /**
     * Checks if there is a matching recipe of type {@link #getRecipeType()} that has the given inputs.
     *
     * @param inputA Recipe input a.
     * @param inputB Recipe input b.
     * @param inputC Recipe input c.
     *
     * @return {@code true} if there is a match, {@code false} if there isn't.
     *
     * @apiNote See {@link TripleInputRecipeCache#containsInputABC(Level, Object, Object, Object)}, {@link TripleInputRecipeCache#containsInputBAC(Level, Object, Object,
     * Object)}, and {@link TripleInputRecipeCache#containsInputCAB(Level, Object, Object, Object)} for more details about when this method should be called versus when
     * {@link #containsRecipeABC(Object, Object, Object)} or {@link #containsRecipeCAB(Object, Object, Object)} should be called.
     */
    default boolean containsRecipeBAC(INPUT_A inputA, INPUT_B inputB, INPUT_C inputC) {
        return getRecipeType().getInputCache().containsInputBAC(getHandlerWorld(), inputA, inputB, inputC);
    }

    /**
     * Checks if there is a matching recipe of type {@link #getRecipeType()} that has the given inputs.
     *
     * @param inputA Recipe input a.
     * @param inputB Recipe input b.
     * @param inputC Recipe input c.
     *
     * @return {@code true} if there is a match, {@code false} if there isn't.
     *
     * @apiNote See {@link TripleInputRecipeCache#containsInputABC(Level, Object, Object, Object)}, {@link TripleInputRecipeCache#containsInputBAC(Level, Object, Object,
     * Object)}, and {@link TripleInputRecipeCache#containsInputCAB(Level, Object, Object, Object)} for more details about when this method should be called versus when
     * {@link #containsRecipeABC(Object, Object, Object)} or {@link #containsRecipeBAC(Object, Object, Object)} should be called.
     */
    default boolean containsRecipeCAB(INPUT_A inputA, INPUT_B inputB, INPUT_C inputC) {
        return getRecipeType().getInputCache().containsInputCAB(getHandlerWorld(), inputA, inputB, inputC);
    }

    /**
     * Checks if there is a matching recipe of type {@link #getRecipeType()} that has the given input.
     *
     * @param input Recipe input.
     *
     * @return {@code true} if there is a match, {@code false} if there isn't.
     */
    default boolean containsRecipeA(INPUT_A input) {
        return getRecipeType().getInputCache().containsInputA(getHandlerWorld(), input);
    }

    /**
     * Checks if there is a matching recipe of type {@link #getRecipeType()} that has the given input.
     *
     * @param input Recipe input.
     *
     * @return {@code true} if there is a match, {@code false} if there isn't.
     */
    default boolean containsRecipeB(INPUT_B input) {
        return getRecipeType().getInputCache().containsInputB(getHandlerWorld(), input);
    }

    /**
     * Checks if there is a matching recipe of type {@link #getRecipeType()} that has the given input.
     *
     * @param input Recipe input.
     *
     * @return {@code true} if there is a match, {@code false} if there isn't.
     */
    default boolean containsRecipeC(INPUT_C input) {
        return getRecipeType().getInputCache().containsInputC(getHandlerWorld(), input);
    }

    /**
     * Finds the first recipe for the type of recipe we handle ({@link #getRecipeType()}) by looking up the given inputs against the recipe type's input cache.
     *
     * @param inputA Recipe input a.
     * @param inputB Recipe input b.
     * @param inputC Recipe input c.
     *
     * @return Recipe matching the given inputs, or {@code null} if no recipe matches.
     */
    @Nullable
    default RECIPE findFirstRecipe(INPUT_A inputA, INPUT_B inputB, INPUT_C inputC) {
        return getRecipeType().getInputCache().findFirstRecipe(getHandlerWorld(), inputA, inputB, inputC);
    }

    /**
     * Finds the first recipe for the type of recipe we handle ({@link #getRecipeType()}) by looking up the given inputs against the recipe type's input cache.
     *
     * @param inputAHandler Input handler to grab the first recipe input from.
     * @param inputBHandler Input handler to grab the second recipe input from.
     * @param inputCHandler Input handler to grab the third recipe input from.
     *
     * @return Recipe matching the given inputs, or {@code null} if no recipe matches.
     */
    @Nullable
    default RECIPE findFirstRecipe(IInputHandler<INPUT_A> inputAHandler, IInputHandler<INPUT_B> inputBHandler, IInputHandler<INPUT_C> inputCHandler) {
        return findFirstRecipe(inputAHandler.getInput(), inputBHandler.getInput(), inputCHandler.getInput());
    }

    /**
     * Helper interface to make the generics that we have to pass to {@link ITripleRecipeLookupHandler} not as messy, and reduce the duplicate code in the other chemical
     * based helper interfaces.
     */
    interface ObjectObjectChemicalRecipeLookupHandler<INPUT_A, INPUT_B, CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>,
          RECIPE extends MekanismRecipe & TriPredicate<INPUT_A, INPUT_B, STACK>, INPUT_CACHE extends TripleInputRecipeCache<INPUT_A, ?, INPUT_B, ?, STACK, ?, RECIPE, ?, ?, ?>>
          extends ITripleRecipeLookupHandler<INPUT_A, INPUT_B, STACK, RECIPE, INPUT_CACHE> {

        /**
         * Helper wrapper to convert a chemical to a chemical stack and pass it to {@link #containsRecipeCAB(Object, Object, Object)} to make validity predicates easier
         * and cleaner.
         */
        default boolean containsRecipeCAB(INPUT_A inputA, INPUT_B inputB, CHEMICAL inputC) {
            return containsRecipeCAB(inputA, inputB, (STACK) inputC.getStack(1));
        }

        /**
         * Helper wrapper to convert a chemical to a chemical stack and pass it to {@link #containsRecipeC(Object)} to make validity predicates easier and cleaner.
         */
        default boolean containsRecipeC(CHEMICAL input) {
            return containsRecipeC((STACK) input.getStack(1));
        }
    }

    /**
     * Helper interface to make the generics that we have to pass to {@link ITripleRecipeLookupHandler} not as messy.
     */
    interface ItemFluidChemicalRecipeLookupHandler<CHEMICAL extends Chemical<CHEMICAL>, STACK extends ChemicalStack<CHEMICAL>, RECIPE extends MekanismRecipe &
          TriPredicate<ItemStack, FluidStack, STACK>> extends ObjectObjectChemicalRecipeLookupHandler<ItemStack, FluidStack, CHEMICAL, STACK, RECIPE,
          ItemFluidChemical<CHEMICAL, STACK, RECIPE>> {
    }
}