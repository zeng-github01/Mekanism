package mekanism.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = Mekanism.MODID)
public class CardboardArmorHandler {

    @SubscribeEvent
    public static void playerHitboxChangesWhenHidingAsBox(TickEvent.PlayerTickEvent event) {
        Entity entity = event.player;
        if (entity instanceof EntityPlayer player) {
            if (player.isAddedToWorld()) {
                if (testForStealth(player)) {
                    player.setSize(0.6F, 0.8F);
                    player.eyeHeight = 0.6F;
                } else {
                    player.setSize(0.6F, 1.8F);
                    player.eyeHeight = 1.62F;
                }
            } else {
                player.setSize(0.6F, 1.8F);
                player.eyeHeight = 1.62F;
            }
        }
    }

    @SubscribeEvent
    public static void playersStealthWhenWearingCardboard(PlayerEvent.Visibility event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (!testForStealth(entity))
            return;
        event.modifyVisibility(0);
    }

    @SubscribeEvent
    public static void mobsMayLoseTargetWhenItIsWearingCardboard(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity.ticksExisted % 16 != 0) {
            return;
        }
        if (entity instanceof EntityLiving mob) {
            setStop(mob);
            if (mob instanceof EntityPigZombie zombie) {
                zombie.angerTargetUUID = null;
                zombie.angerLevel = 0;
            }
        }
    }

    private static void setStop(EntityLiving entity) {
        if (testForStealth(entity.getAttackTarget())) {
            entity.setAttackTarget(null);
            if (entity.targetTasks != null) {
                entity.targetTasks.taskEntries.forEach(task -> task.action.resetTask());
                entity.targetTasks.executingTaskEntries.forEach(task -> task.action.resetTask());
            }
        }

        if (testForStealth(entity.getRevengeTarget())) {
            entity.setAttackTarget(null);
            entity.setRevengeTarget(null);
            entity.attackingPlayer = null;
        }
    }


    public static boolean testForStealth(Entity entityIn) {
        if (!(entityIn instanceof EntityLivingBase entity)) {
            return false;
        }
        if (!entity.isSneaking()) {
            return false;
        }
        if (entity instanceof EntityPlayer player && player.capabilities.isFlying) {
            return false;
        }
        if (entity.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() != MekanismItems.CARDBOARD_HELMET) {
            return false;
        }
        if (entity.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() != MekanismItems.CARDBOARD_CHESTPLATE) {
            return false;
        }
        if (entity.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() != MekanismItems.CARDBOARD_LEGGINGS) {
            return false;
        }
        if (entity.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() != MekanismItems.CARDBOARD_BOOTS) {
            return false;
        }
        return true;
    }

}
