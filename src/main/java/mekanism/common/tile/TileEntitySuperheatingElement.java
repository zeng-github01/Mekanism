package mekanism.common.tile;

import mekanism.common.Mekanism;
import mekanism.common.content.boiler.SynchronizedBoilerData;
import mekanism.common.multiblock.TileEntityInternalMultiblock;
import mekanism.common.util.MekanismUtils;

public class TileEntitySuperheatingElement extends TileEntityInternalMultiblock {

    public boolean prevHot;

    @Override
    public void doRestrictedTick() {
        super.doRestrictedTick();
    }

    @Override
    public void setMultiblock(String id) {
        boolean packet = false;
        if (id == null && multiblockUUID != null) {
            SynchronizedBoilerData.clientHotMap.remove(multiblockUUID);
            packet = true;
        } else if (id != null && multiblockUUID == null) {
            packet = true;
        }

        super.setMultiblock(id);

        if (packet && !isRemote()) {
            Mekanism.packetHandler.sendUpdatePacket(this);
        }
    }

    @Override
    public void onUpdateClient() {
        super.onUpdateClient();
        boolean newHot = false;
        if (multiblockUUID != null && SynchronizedBoilerData.clientHotMap.get(multiblockUUID) != null) {
            newHot = SynchronizedBoilerData.clientHotMap.get(multiblockUUID);
        }
        if (prevHot != newHot) {
            MekanismUtils.updateBlock(world, getPos());
            prevHot = newHot;
        }
    }
}
