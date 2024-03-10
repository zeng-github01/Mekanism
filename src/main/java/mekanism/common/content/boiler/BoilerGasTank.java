package mekanism.common.content.boiler;

import mekanism.api.Coord4D;
import mekanism.common.base.MultiblockGasTank;
import mekanism.common.content.tank.SynchronizedTankData.ValveData;
import mekanism.common.tile.multiblock.TileEntityBoilerCasing;

public abstract class BoilerGasTank extends MultiblockGasTank<TileEntityBoilerCasing> {

    public BoilerGasTank(TileEntityBoilerCasing tileEntity) {
        super(tileEntity);
    }

    @Override
    protected void updateValveData() {
        if (multiblock.structure != null) {
            Coord4D coord4D = Coord4D.get(multiblock);
            for (ValveData data : multiblock.structure.valves) {
                if (coord4D.equals(data.location)) {
                    data.onTransfer();
                }
            }
        }
    }
}
