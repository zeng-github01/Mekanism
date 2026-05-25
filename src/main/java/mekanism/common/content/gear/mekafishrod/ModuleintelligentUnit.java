package mekanism.common.content.gear.mekafishrod;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.common.MekanismModules;
import mekanism.common.util.LangUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

@ParametersAreNotNullByDefault
public class ModuleintelligentUnit implements ICustomModule<ModuleintelligentUnit> {

    @Override
    public boolean canChangeModeWhenDisabled(IModule<ModuleintelligentUnit> module) {
        return true;
    }

    @Override
    public void changeMode(IModule<ModuleintelligentUnit> module, EntityPlayer player, ItemStack stack, int shift, boolean displayChangeMessage) {
        module.toggleEnabled(player, LangUtils.localize(MekanismModules.FISHING_INTELLIGENT_UNIT.getTranslationKey()));
    }

    @Override
    public void tickServerUpdate(IModule<ModuleintelligentUnit> module, ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        if (!isSelected || !(entity instanceof EntityPlayer player) || player.getHeldItemMainhand() != stack) {
            return;
        }
        autoFish(world, player, EnumHand.MAIN_HAND);
    }

    private void autoFish(World world, EntityPlayer player, EnumHand hand) {
        EntityFishHook fishHook = player.fishEntity;
        if (fishHook == null) {
            player.getHeldItem(hand).useItemRightClick(world, player, hand);
        } else if (fishHook.ticksCatchable > 0 || fishHook.caughtEntity != null) {
            player.getHeldItem(hand).useItemRightClick(world, player, hand);
        }
    }
}
