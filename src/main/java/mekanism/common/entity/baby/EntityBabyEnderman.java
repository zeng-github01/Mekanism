package mekanism.common.entity.baby;

import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class EntityBabyEnderman extends EntityEnderman implements IBabyEntity {

    private static final DataParameter<Boolean> IS_CHILD = EntityDataManager.createKey(EntityBabyEnderman.class, DataSerializers.BOOLEAN);

    public EntityBabyEnderman(World worldIn) {
        super(worldIn);
        dataManager.register(IS_CHILD, false);
        setChild(IS_CHILD, true);
        setSize(0.5F, 1.5F);
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
}
