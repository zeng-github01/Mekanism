package mekanism.common.entity.baby;

import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public class EntityBabyStray extends EntityStray implements IBabyEntity {

    private static final DataParameter<Boolean> IS_CHILD = EntityDataManager.createKey(EntityBabyStray.class, DataSerializers.BOOLEAN);

    public EntityBabyStray(World worldIn) {
        super(worldIn);
        dataManager.register(IS_CHILD, false);
        setChild(IS_CHILD, true);
        setSize(0.5F, 1.125F);
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
        return 0.90625F;
    }
}
