package mekanism.common.content.boiler;

import mekanism.api.gas.GasStack;
import mekanism.common.tile.multiblock.TileEntityBoilerCasing;

import javax.annotation.Nullable;

public class BoilerInputGasTank extends BoilerGasTank {

    public BoilerInputGasTank(TileEntityBoilerCasing tileEntity) {
        super(tileEntity);
    }

    @Override
    @Nullable
    public GasStack getGas() {
        return multiblock.structure != null ? multiblock.structure.InputGas : null;
    }

    @Override
    public void setGas(GasStack stack) {
        if (multiblock.structure != null) {
            multiblock.structure.InputGas = stack;
        }
    }

    @Override
    public int getMaxGas() {
        return multiblock.structure != null ? multiblock.structure.waterVolume * BoilerUpdateProtocol.WATER_PER_TANK : 0;
    }
}
