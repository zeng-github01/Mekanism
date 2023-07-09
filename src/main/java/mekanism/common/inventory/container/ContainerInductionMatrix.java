package mekanism.common.inventory.container;

import mekanism.common.inventory.slot.SlotArmor;
import mekanism.common.inventory.slot.SlotEnergy.SlotCharge;
import mekanism.common.inventory.slot.SlotEnergy.SlotDischarge;
import mekanism.common.tile.TileEntityInductionCasing;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;

public class ContainerInductionMatrix extends ContainerEnergyStorage<TileEntityInductionCasing> {

    public ContainerInductionMatrix(InventoryPlayer inventory, TileEntityInductionCasing tile) {
        super(tile, inventory);
    }

    @Override
    protected void addSlots() {
        addSlotToContainer(new SlotCharge(tileEntity, 0, 146, 20));
        addSlotToContainer(new SlotDischarge(tileEntity, 1, 146, 51));
    }

    @Override
    protected void addPlayerArmmorSlot(InventoryPlayer inventory) {
        addSlotToContainer(new SlotArmor(inventory, EntityEquipmentSlot.HEAD, -20, 37 + 5));
        addSlotToContainer(new SlotArmor(inventory, EntityEquipmentSlot.CHEST, -20, 37 + 23));
        addSlotToContainer(new SlotArmor(inventory, EntityEquipmentSlot.LEGS, -20, 37 + 41));
        addSlotToContainer(new SlotArmor(inventory, EntityEquipmentSlot.FEET, -20, 37 + 59));
        addSlotToContainer(new SlotArmor(inventory, EntityEquipmentSlot.OFFHAND, -20, 37 + 77));
    }
}