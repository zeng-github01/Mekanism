package mekanism.common.recipe.outputs;

import mekanism.api.gas.GasStack;
import mekanism.api.gas.GasTank;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Random;

public class ChanceGasOutput extends MachineOutput<ChanceGasOutput> {

    private static Random rand = new Random();

    public GasStack output;
    public double primaryChance;
    public ChanceGasOutput(GasStack stack, double chance) {
        output = stack;
        primaryChance = chance;
    }

    public ChanceGasOutput() {
    }

    @Override
    public void load(NBTTagCompound nbtTags) {
        output = GasStack.readFromNBT(nbtTags.getCompoundTag("output"));
        primaryChance = nbtTags.getDouble("primaryChance");
    }

    public boolean checkSecondary() {
        return rand.nextDouble() <= primaryChance;
    }

    @Override
    public ChanceGasOutput copy() {
        return new ChanceGasOutput(output.copy(),primaryChance);
    }

    public boolean applyOutputs(GasTank gasTank, boolean doEmit, int scale) {
        if (gasTank.canReceive(output.getGas()) && gasTank.getNeeded() >= output.amount * scale && checkSecondary()) {
            gasTank.receive(output.copy().withAmount(output.amount * scale), doEmit);
            return true;
        }
        return false;
    }
}
