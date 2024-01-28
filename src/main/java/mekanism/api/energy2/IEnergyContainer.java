package mekanism.api.energy2;

import mekanism.api.IContentsListener;
import mekanism.api.math.FloatingLong;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IEnergyContainer extends INBTSerializable<NBTTagCompound>, IContentsListener {

    double getEnergy();

    void setEnergy(double energy);

}
