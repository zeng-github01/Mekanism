package mekanism.common.inventory.slot;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SlotArmor extends Slot {

    private final EntityEquipmentSlot armorType;

    public SlotArmor(InventoryPlayer inventory, EntityEquipmentSlot armorType, int x, int y) {
        super(inventory, 35 + armorType.getSlotIndex(), x, y);
        this.armorType = armorType;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        Item item = stack.getItem();
        if (armorType != EntityEquipmentSlot.OFFHAND) {
            return item.isValidArmor(stack, armorType, ((InventoryPlayer) inventory).player);
        }
        return true;
    }


    @SideOnly(Side.CLIENT)
    public String getSlotTexture() {
        return armorType != EntityEquipmentSlot.OFFHAND ? ItemArmor.EMPTY_SLOT_NAMES[armorType.getIndex()] : "minecraft:items/empty_armor_slot_shield";
    }
}
