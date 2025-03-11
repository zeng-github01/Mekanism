package mekanism.common.integration.lookingat;

import mekanism.api.gas.GasStack;
import net.minecraftforge.fluids.FluidStack;

public interface LookingAtHelper {

    void addText(String text);

    void addEnergyElement(double energy, double maxEnergy);

    void addFluidElement(FluidStack stored, int capacity);

    void addChemicalElement(GasStack stored, int capacity);
}
