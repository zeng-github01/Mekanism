package mekanism.api.gas;

import javax.annotation.Nullable;

public interface IGasTank {

    @Nullable
    GasStack getGas();

    int getGasAmount();

    int getMaxGas();

    GasTankInfo getInfo();

    int input(GasStack resource, boolean input);

    @Nullable
    GasStack output(int Maxoutput ,boolean output);
}
