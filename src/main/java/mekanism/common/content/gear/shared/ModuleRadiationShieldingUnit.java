package mekanism.common.content.gear.shared;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.common.config.MekanismConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

@ParametersAreNotNullByDefault
public class ModuleRadiationShieldingUnit implements ICustomModule<ModuleRadiationShieldingUnit> {


    @Override
    public void onAdded(IModule<ModuleRadiationShieldingUnit> module, boolean first) {
        double ncRadiationResistance = MekanismConfig.current().meka.mekaSuitModuleRadiationresistance.val();
        ItemStack stack = module.getContainer();
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            stack.setTagCompound(new NBTTagCompound());
        }
        if (first) {
            if (!tag.hasKey("ncRadiationResistance")) {
                tag.setDouble("ncRadiationResistance", ncRadiationResistance);
            }
        } else {
            if (tag != null) {
                tag.setDouble("ncRadiationResistance", ncRadiationResistance * module.getInstalledCount());
            }
        }
    }

    @Override
    public void onRemoved(IModule<ModuleRadiationShieldingUnit> module, boolean last) {
        double ncRadiationResistance = MekanismConfig.current().meka.mekaSuitModuleRadiationresistance.val();
        ItemStack stack = module.getContainer();
        NBTTagCompound tag = stack.getTagCompound();
        if (last) {
            if (tag != null) {
                tag.removeTag("ncRadiationResistance");
            }
        } else {
            if (tag != null) {
                tag.setDouble("ncRadiationResistance", ncRadiationResistance * module.getInstalledCount());
            }
        }
    }


}
