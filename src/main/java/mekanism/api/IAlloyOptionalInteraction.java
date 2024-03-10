package mekanism.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Implement this class in your TileEntity if it can interact with Mekanism alloys.
 *
 * @author aidancbrady
 */
public interface IAlloyOptionalInteraction<T> {

    /**
     * Called when a player right-clicks this block with an alloy.
     *
     * @param player - the player right-clicking the block
     * @param stack  - the stack of alloy being right-clicked
     * @param tier   - the tier of the alloy
     */
    void onAlloyInteraction(EntityPlayer player, ItemStack stack, @NotNull T tier);
}
