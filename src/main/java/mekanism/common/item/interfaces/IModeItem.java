package mekanism.common.item.interfaces;

import mekanism.api.EnumColor;
import mekanism.api.text.TextComponentGroup;
import mekanism.client.render.hud.MekanismStatusOverlay;
import mekanism.common.Mekanism;
import mekanism.common.lib.radial.IGenericRadialModeItem;
import mekanism.common.network.PacketShowModeChange.ShowModeChangeMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface IModeItem {

    void changeMode(@NotNull EntityPlayer player, @NotNull ItemStack stack, int shift, DisplayChange displayChangeMessage);

    default boolean supportsSlotType(ItemStack stack, @NotNull EntityEquipmentSlot slotType) {
        return slotType == EntityEquipmentSlot.MAINHAND || slotType == EntityEquipmentSlot.OFFHAND;
    }

    @Nullable
    default ITextComponent getScrollTextComponent(@NotNull ItemStack stack) {
        return null;
    }

    static boolean isModeItem(@NotNull EntityPlayer player, @NotNull EntityEquipmentSlot slotType) {
        return isModeItem(player, slotType, true);
    }

    static boolean isModeItem(@NotNull EntityPlayer player, @NotNull EntityEquipmentSlot slotType, boolean allowRadial) {
        return isModeItem(player.getItemStackFromSlot(slotType), slotType, allowRadial);
    }

    static boolean isModeItem(@NotNull ItemStack stack, @NotNull EntityEquipmentSlot slotType) {
        return isModeItem(stack, slotType, true);
    }

    static boolean isModeItem(@NotNull ItemStack stack, @NotNull EntityEquipmentSlot slotType, boolean allowRadial) {
        if (!stack.isEmpty() && stack.getItem() instanceof IModeItem modeItem && modeItem.supportsSlotType(stack, slotType)) {
            return allowRadial || !(modeItem instanceof IGenericRadialModeItem radialModeItem) || radialModeItem.getRadialData(stack) == null;
        }
        return false;
    }

    static void displayModeChange(EntityPlayer player) {
        if (player instanceof EntityPlayerMP serverPlayer) {
            Mekanism.packetHandler.sendTo(ShowModeChangeMessage.INSTANCE, serverPlayer);
        } else {
            MekanismStatusOverlay.INSTANCE.setTimer();
        }
    }


    enum DisplayChange {
        NONE,
        MAIN_HAND,
        OTHER;

        public void sendMessage(EntityPlayer player, Supplier<ITextComponent> message) {
            if (this == MAIN_HAND) {
                //TODO: Eventually decide if we want to make it so that it checks if IModeItem#getScrollTextComponent is null and otherwise just make it a system message
                displayModeChange(player);
            } else if (this == OTHER) {
                player.sendMessage(new TextComponentGroup(EnumColor.GREY.textFormatting).string(Mekanism.LOG_TAG, TextFormatting.DARK_BLUE).string(" ").component(message.get()));
            }
        }
    }
}
