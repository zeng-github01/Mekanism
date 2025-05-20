package mekanism.common.capabilities.radiation.item;

import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.ItemCapabilityWrapper.ItemCapability;
import nc.capability.radiation.resistance.IRadiationResistance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional.Interface;

import java.util.Objects;
import java.util.function.ToDoubleFunction;

@Interface(iface = "nc.capability.radiation.resistance.IRadiationResistance", modid = "nuclearcraft")
public class NCRadiationShieldingHandler extends ItemCapability implements IRadiationResistance {

    public static NCRadiationShieldingHandler create(ToDoubleFunction<ItemStack> baseRadResistance, ToDoubleFunction<ItemStack> shieldingRadResistance) {
        Objects.requireNonNull(baseRadResistance, "radiationResistance function cannot be null");
        Objects.requireNonNull(shieldingRadResistance, "shieldingRadResistance function cannot be null");
        return new NCRadiationShieldingHandler(baseRadResistance, shieldingRadResistance);
    }

    private final ToDoubleFunction<ItemStack> baseRadResistance, shieldingRadResistance;

    private NCRadiationShieldingHandler(ToDoubleFunction<ItemStack> baseRadResistance, ToDoubleFunction<ItemStack> shieldingRadResistance) {
        this.baseRadResistance = baseRadResistance;
        this.shieldingRadResistance = shieldingRadResistance;
    }


    @Override
    public boolean canProcess(Capability<?> capability) {
        return capability == Capabilities.NC_CAPABILITY_RADIATION_RESISTANCE;
    }

    public double getBaseRadResistance() {
        return baseRadResistance.applyAsDouble(getStack());
    }


    public void setBaseRadResistance(double v) {
    }

    public double getShieldingRadResistance() {
        return shieldingRadResistance.applyAsDouble(getStack());
    }

    public void setShieldingRadResistance(double v) {
    }

    public NBTTagCompound writeNBT(IRadiationResistance iRadiationResistance, EnumFacing enumFacing, NBTTagCompound nbtTagCompound) {
        return null;
    }

    public void readNBT(IRadiationResistance iRadiationResistance, EnumFacing enumFacing, NBTTagCompound nbtTagCompound) {
    }


    public double getRadiationResistance() {
        return getBaseRadResistance();
    }

    public void setRadiationResistance(double newResistance) {

    }
}
