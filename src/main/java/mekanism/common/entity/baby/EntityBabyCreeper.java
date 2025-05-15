package mekanism.common.entity.baby;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class EntityBabyCreeper extends EntityCreeper implements IBabyEntity {

    private static final DataParameter<Boolean> IS_CHILD = EntityDataManager.createKey(EntityBabyCreeper.class, DataSerializers.BOOLEAN);

    public EntityBabyCreeper(World worldIn) {
        super(worldIn);
        dataManager.register(IS_CHILD, false);
        setChild(IS_CHILD, true);
        setSize(0.5F, 0.8125F);
    }

    @Override
    public boolean isChild() {
        return dataManager.get(IS_CHILD);
    }


    @Override
    protected int getExperiencePoints(EntityPlayer player) {
        if (isChild()) {
            experienceValue = (int) (experienceValue * 2.5F);
        }
        return super.getExperiencePoints(player);
    }

    @Override
    public float getEyeHeight() {
        return this.height * 0.85F;
    }

    @Override
    protected void explode() {
        if (!world.isRemote) {
            boolean flag = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.world, this);
            float f = this.getPowered() ? 1 : 0.5F;
            dead = true;
            world.createExplosion(this, this.posX, this.posY, this.posZ, explosionRadius * f, flag);
            setDead();
            spawnLingeringCloud();
        }
    }
}
