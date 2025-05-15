package mekanism.common.entity.baby;

import mekanism.common.Mekanism;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityBabyWitherSkeleton extends EntityWitherSkeleton implements IBabyEntity {

    private static final DataParameter<Boolean> IS_CHILD = EntityDataManager.createKey(EntityBabyWitherSkeleton.class, DataSerializers.BOOLEAN);

    public EntityBabyWitherSkeleton(World world) {
        super(world);
        dataManager.register(IS_CHILD, false);
        setChild(IS_CHILD, true);
        setSize(0.5F, 1.375F);
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


    //copied from base entity, as abstractskeleton overrides it
    @Override
    public float getEyeHeight() {
        return 1.09375F;
    }

}
