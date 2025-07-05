package mekanism.client.jei.interfaces;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface IJEIGhostTarget {

    /**
     * @return {@code null} if it doesn't actually currently support ghost handling
     */
    @Nullable
    IGhostIngredientConsumer getGhostHandler();

    /**
     * Number of pixels on each side that make up the border, and should be ignored when creating the target area.
     */
    default int borderSize() {
        return 0;
    }

    interface IGhostIngredientConsumer extends Consumer<Object> {

        boolean supportsIngredient(Object ingredient);
    }

    interface IGhostItemConsumer extends IGhostIngredientConsumer {

        @Override
        default boolean supportsIngredient(Object ingredient) {
            return ingredient instanceof ItemStack stack && !stack.isEmpty();
        }
    }

    interface IGhostBlockItemConsumer extends IGhostItemConsumer {

        @Override
        default boolean supportsIngredient(Object ingredient) {
            //Only allow block items
            return IGhostItemConsumer.super.supportsIngredient(ingredient) && ((ItemStack) ingredient).getItem() instanceof ItemBlock;
        }
    }
}