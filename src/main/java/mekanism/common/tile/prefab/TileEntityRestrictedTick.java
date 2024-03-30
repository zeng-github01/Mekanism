package mekanism.common.tile.prefab;

import net.minecraft.util.ITickable;

public abstract class TileEntityRestrictedTick extends TileEntitySynchronized implements ITickable {

    protected int ticksExisted = 0;
    private long lastUpdateWorldTick = -1;

    @Override
    public final void update() {
        long currentTick = getWorld().getTotalWorldTime();
        if (lastUpdateWorldTick == currentTick) {
            return;
        }
        lastUpdateWorldTick = currentTick;
        doRestrictedTick();
        ticksExisted++;
    }

    public abstract void doRestrictedTick();
}
