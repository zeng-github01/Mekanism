package mekanism.common.tile.machine;

import java.util.HashSet;
import java.util.Set;

import mekanism.common.MekanismItems;
import mekanism.common.Upgrade;
import mekanism.common.base.IRedstoneControl;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.block.states.BlockStateMachine;
import mekanism.common.chunkloading.IChunkLoader;
import mekanism.common.security.ISecurityTile;
import mekanism.common.tile.component.TileComponentChunkLoader;
import mekanism.common.tile.component.TileComponentSecurity;
import mekanism.common.tile.component.TileComponentUpgrade;
import mekanism.common.tile.prefab.TileEntityMachine;
import mekanism.common.util.ChargeUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.ChunkPos;
import org.jetbrains.annotations.NotNull;

public class TileEntityDimensionalStabilizer extends TileEntityMachine implements IChunkLoader {

    public static final int MAX_LOAD_RADIUS = 2;
    public static final int MAX_LOAD_DIAMETER = 2 * MAX_LOAD_RADIUS + 1;

    public TileComponentChunkLoader chunkLoaderComponent;

    private final boolean[][] loadingChunks;

    private int chunksLoaded = 1;
    public boolean clientRendering;


    public TileEntityDimensionalStabilizer() {
        super("DimensionalStabilizer", BlockStateMachine.MachineType.DIMENSIONAL_STABILIZER,1);
        inventory = NonNullList.withSize(2, ItemStack.EMPTY);
        upgradeComponent.setSupported(Upgrade.SPEED,false);
        chunkLoaderComponent = new TileComponentChunkLoader(this);
        loadingChunks = new boolean[MAX_LOAD_DIAMETER][MAX_LOAD_DIAMETER];
        //Center chunk where the stabilizer is, is always loaded (unless none are loaded due to energy or control mode)
        loadingChunks[MAX_LOAD_RADIUS][MAX_LOAD_RADIUS] = true;

    }

    @Override
    public TileComponentChunkLoader getChunkLoader() {
        return chunkLoaderComponent;
    }

    public boolean isChunkLoadingAt(int x, int z) {
        return loadingChunks[x][z];
    }

    private boolean setChunkLoadingAt(int x, int z, boolean load) {
        if (x == MAX_LOAD_RADIUS && z == MAX_LOAD_RADIUS) {
            //Center chunk where the stabilizer is, is always loaded (unless none are loaded due to energy or control mode)
            // so just skip and return we don't need to update if that is the position that someone attempts to change
            return false;
        } else if (isChunkLoadingAt(x, z) != load) {
            loadingChunks[x][z] = load;
            if (load) {
                chunksLoaded++;
            } else {
                chunksLoaded--;
            }
            return true;
        }
        return false;
    }

    public void toggleChunkLoadingAt(int x, int z) {
        //Validate x and z are valid as this is called from a packet
        if (x >= 0 && x < MAX_LOAD_DIAMETER && z >= 0 && z < MAX_LOAD_DIAMETER) {
            if (setChunkLoadingAt(x, z, !isChunkLoadingAt(x, z))) {
                setEnergy(getEnergy() - getPerTick());
                //Refresh the chunks that are loaded as it has changed
                getChunkLoader().refreshChunkSet();
            }
        }
    }

    public double getPerTick() {
        return energyPerTick;
    }

    @Override
    public Set<ChunkPos> getChunkSet() {
        Set<ChunkPos> chunkSet = new HashSet<>();
        int chunkX = blockToSectionCoord(pos.getX());
        int chunkZ = blockToSectionCoord(pos.getZ());
        for (int x = -MAX_LOAD_RADIUS; x <= MAX_LOAD_RADIUS; x++) {
            for (int z = -MAX_LOAD_RADIUS; z <= MAX_LOAD_RADIUS; z++) {
                if (isChunkLoadingAt(x + MAX_LOAD_RADIUS, z + MAX_LOAD_RADIUS)) {
                    chunkSet.add(new ChunkPos(chunkX + x, chunkZ + z));
                }
            }
        }
        return chunkSet;
    }

    @Override
    public boolean isItemValidForSlot(int slotID, @NotNull ItemStack itemstack) {
        if (slotID == 0){
            return ChargeUtils.canBeDischarged(itemstack);
        }

        if (slotID == 1) {
            return itemstack.getItem() == MekanismItems.EnergyUpgrade;
        }

        return false;
    }

    @NotNull
    @Override
    public int[] getSlotsForFace(@NotNull EnumFacing side) {
        return new int[0];
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!world.isRemote) {
            ChargeUtils.discharge(0, this);
        }
    }

    public static int blockToSectionCoord(int pBlockCoord) {
        return pBlockCoord >> 4;
    }
}
