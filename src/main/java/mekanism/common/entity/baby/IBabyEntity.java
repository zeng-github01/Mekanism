package mekanism.common.entity.baby;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.network.datasync.DataParameter;

import java.util.UUID;

public interface IBabyEntity {

    //COPY of ZombieEntity BABY_SPEED_BOOST_ID and BABY_SPEED_BOOST
    UUID babySpeedBoostUUID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
    AttributeModifier babySpeedBoostModifier = new AttributeModifier(babySpeedBoostUUID, "Baby speed boost", 0.5D, 1);

    default void setChild(DataParameter<Boolean> childParameter, boolean child) {
        EntityLivingBase entity = (EntityLivingBase) this;
        entity.getDataManager().set(childParameter, child);
        if (entity.world != null && !entity.world.isRemote) {
            IAttributeInstance attributeInstance = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
            if (attributeInstance != null) {
                attributeInstance.removeModifier(babySpeedBoostModifier);
                if (child) {
                    attributeInstance.applyModifier(babySpeedBoostModifier);
                }
            }
        }
    }
}
