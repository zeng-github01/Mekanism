package mekanism.common.inventory.container;

import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.content.gear.IModuleItem;
import mekanism.common.inventory.slot.SlotArmor;
import mekanism.common.inventory.slot.SlotEnergy;
import mekanism.common.tile.TileEntityModificationStation;
import mekanism.common.util.ChargeUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ContainerModificationStation extends ContainerMekanism<TileEntityModificationStation> {


    public ContainerModificationStation(InventoryPlayer inventory, TileEntityModificationStation tile) {
        super(tile, inventory);
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
        ItemStack stack = ItemStack.EMPTY;
        Slot currentSlot = inventorySlots.get(slotID);
        if (currentSlot != null && currentSlot.getHasStack()) {
            ItemStack slotStack = currentSlot.getStack();
            stack = slotStack.copy();
            if (slotID < 3) {
                if (!mergeItemStack(slotStack, 3, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotStack.getItem() instanceof IModuleContainerItem) {
                if (!mergeItemStack(slotStack, 2, 3, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotStack.getItem() instanceof IModuleItem) {
                if (!mergeItemStack(slotStack, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (ChargeUtils.canBeDischarged(slotStack)) {
                if (!mergeItemStack(slotStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotID >= 3 && slotID <= 29) {
                if (!mergeItemStack(slotStack, 30, inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotID > 29) {
                if (!mergeItemStack(slotStack, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(slotStack, 3, inventorySlots.size(), true)) {
                return ItemStack.EMPTY;
            }
            if (slotStack.getCount() == 0) {
                currentSlot.putStack(ItemStack.EMPTY);
            } else {
                currentSlot.onSlotChanged();
            }
            if (slotStack.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }
            currentSlot.onTake(player, slotStack);
        }
        return stack;
    }

    @Override
    protected void addSlots() {
        addSlotToContainer(new SlotEnergy.SlotDischarge(tileEntity, 1, 149, 21));
        addSlotToContainer(new Slot(tileEntity, 2, 35, 118) {
            @Override
            public boolean isItemValid(ItemStack itemstack) {
                return itemstack.getItem() instanceof IModuleItem;
            }
        });
        addSlotToContainer(new Slot(tileEntity, 3, 125, 118) {
            @Override
            public boolean isItemValid(ItemStack itemstack) {
                return itemstack.getItem() instanceof IModuleContainerItem;
            }
        });
    }

    @Override
    protected void addPlayerArmmorSlot(InventoryPlayer inventory) {
        addSlotToContainer(new SlotArmor(inventory, EntityEquipmentSlot.HEAD, -20, 20 + 5));
        addSlotToContainer(new SlotArmor(inventory, EntityEquipmentSlot.CHEST, -20, 20 + 23));
        addSlotToContainer(new SlotArmor(inventory, EntityEquipmentSlot.LEGS, -20, 20 + 41));
        addSlotToContainer(new SlotArmor(inventory, EntityEquipmentSlot.FEET, -20, 20 + 59));
        addSlotToContainer(new SlotArmor(inventory, EntityEquipmentSlot.OFFHAND, -20, 20 + 77));
    }

    @Override
    protected int getInventorYOffset() {
        return 148;
    }
}
