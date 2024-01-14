package mekanism.common.inventory.container;

import mekanism.common.tile.prefab.TileEntityContainerBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

import javax.annotation.Nonnull;

public abstract class ContainerMekanism<TILE extends TileEntityContainerBlock> extends Container {

    protected TILE tileEntity;

    protected ContainerMekanism(TILE tile, InventoryPlayer inventory) {
        this.tileEntity = tile;
        if (shouldAddSlots()) {
            addSlots();
            if (inventory != null) {
                addInventorySlots(inventory);
                addPlayerArmmorSlot(inventory);
                openInventory(inventory);
            }
        }
    }

    protected int getInventorYOffset() {
        return 84;
    }

    protected int getInventorXOffset() {
        return 8;
    }

    protected void addInventorySlots(InventoryPlayer inventory) {
        int yOffset = getInventorYOffset();
        int xOffset = getInventorXOffset();
        for (int slotY = 0; slotY < 3; slotY++) {
            for (int slotX = 0; slotX < 9; slotX++) {
                addSlotToContainer(new Slot(inventory, slotX + slotY * 9 + 9, xOffset + slotX * 18, yOffset + slotY * 18));
            }
        }
        yOffset += 58;
        for (int slotY = 0; slotY < 9; slotY++) {
            addSlotToContainer(new Slot(inventory, slotY, xOffset + slotY * 18, yOffset));
        }
    }

    protected void addPlayerArmmorSlot(InventoryPlayer inventory) {
    }

    protected boolean shouldAddSlots() {
        return true;
    }

    protected abstract void addSlots();

    protected void openInventory(InventoryPlayer inventory) {
        if (tileEntity != null) {
            tileEntity.open(inventory.player);
            tileEntity.openInventory(inventory.player);
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer entityplayer) {
        super.onContainerClosed(entityplayer);
        closeInventory(entityplayer);
    }

    protected void closeInventory(EntityPlayer entityplayer) {
        if (tileEntity != null) {
            tileEntity.close(entityplayer);
            tileEntity.closeInventory(entityplayer);
        }
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer entityplayer) {
        return tileEntity == null || tileEntity.isUsableByPlayer(entityplayer);
    }
}
