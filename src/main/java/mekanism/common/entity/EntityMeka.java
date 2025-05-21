package mekanism.common.entity;

import mekanism.common.content.gear.IModuleContainerItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityMeka extends EntityItem {

    public EntityMeka(World world, Entity original, ItemStack stack) {
        super(world, original.posX, original.posY, original.posZ, stack);
        motionX = original.motionX;
        motionY = original.motionY;
        motionZ = original.motionZ;
        isImmuneToFire = true;
        setPickupDelay(40);
    }


    @Override
    protected void dealFireDamage(int damage) {
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        return source.getDamageType().equals("outOfWorld");
    }

    @Override
    public void onUpdate() {
        ItemStack stack = getItem();
        if (stack.getItem() instanceof IModuleContainerItem moule) {
            moule.getModules(stack).forEach(module -> module.tickItem(this));
        }
        super.onUpdate();
    }


}
