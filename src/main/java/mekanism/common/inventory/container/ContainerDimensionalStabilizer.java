package mekanism.common.inventory.container;

import mekanism.common.inventory.slot.SlotEnergy;
import mekanism.common.tile.machine.TileEntityDimensionalStabilizer;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerDimensionalStabilizer extends ContainerMekanism<TileEntityDimensionalStabilizer>{
    public ContainerDimensionalStabilizer(TileEntityDimensionalStabilizer tileEntityDimensionalStabilizer, InventoryPlayer inventory) {
        super(tileEntityDimensionalStabilizer, inventory);
    }

    @Override
    protected void addSlots() {
        addSlotToContainer(new SlotEnergy.SlotDischarge(tileEntity,0,6,12));
    }
}
