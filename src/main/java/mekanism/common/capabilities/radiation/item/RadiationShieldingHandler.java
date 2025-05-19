package mekanism.common.capabilities.radiation.item;

import mekanism.api.radiation.capability.IRadiationShielding;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.capabilities.ItemCapabilityWrapper.ItemCapability;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;

import java.util.Objects;
import java.util.function.ToDoubleFunction;

public class RadiationShieldingHandler extends ItemCapability implements IRadiationShielding {

    public static RadiationShieldingHandler create(ToDoubleFunction<ItemStack> shieldingFunction) {
        Objects.requireNonNull(shieldingFunction, "Shielding function cannot be null");
        return new RadiationShieldingHandler(shieldingFunction);
    }

    private final ToDoubleFunction<ItemStack> shieldingFunction;

    private RadiationShieldingHandler(ToDoubleFunction<ItemStack> shieldingFunction) {
        this.shieldingFunction = shieldingFunction;
    }

    @Override
    public double getRadiationShielding() {
        return shieldingFunction.applyAsDouble(getStack());
    }

    @Override
    public boolean canProcess(Capability<?> capability) {
        return capability == Capabilities.RADIATION_SHIELDING_CAPABILITY;
    }
}
