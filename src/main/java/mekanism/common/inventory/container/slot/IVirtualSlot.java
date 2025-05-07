package mekanism.common.inventory.container.slot;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.IntSupplier;

public interface IVirtualSlot {

    int getActualX();

    int getActualY();

    void updatePosition(IntSupplier xPositionSupplier, IntSupplier yPositionSupplier);

    void updateRenderInfo(@Nonnull ItemStack stackToRender, boolean shouldDrawOverlay, @Nullable String tooltipOverride);

    @Nonnull
    ItemStack getStackToRender();

    boolean shouldDrawOverlay();

    @Nullable
    String getTooltipOverride();

    Slot getSlot();
}