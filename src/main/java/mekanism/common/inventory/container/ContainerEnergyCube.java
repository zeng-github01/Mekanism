package mekanism.common.inventory.container;

import mekanism.common.inventory.slot.SlotArmor;
import mekanism.common.inventory.slot.SlotEnergy.SlotCharge;
import mekanism.common.inventory.slot.SlotEnergy.SlotDischarge;
import mekanism.common.tile.TileEntityEnergyCube;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;

public class ContainerEnergyCube extends ContainerEnergyStorage<TileEntityEnergyCube> {

    public ContainerEnergyCube(InventoryPlayer inventory, TileEntityEnergyCube tile) {
        super(tile, inventory);
    }

    @Override
    protected void addSlots() {
        addSlotToContainer(new SlotCharge(tileEntity, 0, 143, 35));
        addSlotToContainer(new SlotDischarge(tileEntity, 1, 17, 35));
    }

    @Override
    protected void addPlayerArmmorSlot(InventoryPlayer inventory) {
        addSlotToContainer(new SlotArmor(inventory, EntityEquipmentSlot.HEAD, 180, 37 + 5));
        addSlotToContainer(new SlotArmor(inventory, EntityEquipmentSlot.CHEST, 180, 37 + 23));
        addSlotToContainer(new SlotArmor(inventory, EntityEquipmentSlot.LEGS, 180, 37 + 41));
        addSlotToContainer(new SlotArmor(inventory, EntityEquipmentSlot.FEET, 180, 37 + 59));
        addSlotToContainer(new SlotArmor(inventory, EntityEquipmentSlot.OFFHAND, 180, 37 + 77));
    }
}
