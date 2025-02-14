package mekanism.common;

import mekanism.api.gas.GasStack;
import mekanism.common.entity.EntityFlame;
import mekanism.common.item.*;
import mekanism.common.item.ItemJetpack.JetpackMode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class CommonPlayerTickHandler {

    public static boolean isOnGround(EntityPlayer player) {
        int x = MathHelper.floor(player.posX);
        int y = MathHelper.floor(player.posY - 0.01);
        int z = MathHelper.floor(player.posZ);
        BlockPos pos = new BlockPos(x, y, z);
        IBlockState s = player.world.getBlockState(pos);
        AxisAlignedBB box = s.getBoundingBox(player.world, pos).offset(pos);
        AxisAlignedBB playerBox = player.getEntityBoundingBox();
        return !s.getBlock().isAir(s, player.world, pos) && playerBox.offset(0, -0.01, 0).intersects(box);

    }

    public static boolean isGasMaskOn(EntityPlayer player) {
        ItemStack tank = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        ItemStack mask = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (!tank.isEmpty() && !mask.isEmpty()) {
            if (tank.getItem() instanceof ItemScubaTank scubaTank && mask.getItem() instanceof ItemGasMask) {
                if (scubaTank.getGas(tank) != null) {
                    return scubaTank.getFlowing(tank);
                }
            }
        }
        return false;
    }

    public static boolean isFlamethrowerOn(EntityPlayer player) {
        if (Mekanism.playerState.isFlamethrowerOn(player)) {
            ItemStack currentItem = player.inventory.getCurrentItem();
            return !currentItem.isEmpty() && currentItem.getItem() instanceof ItemFlamethrower;
        }
        return false;
    }

    @SubscribeEvent
    public void onTick(PlayerTickEvent event) {
        if (event.phase == Phase.END && event.side == Side.SERVER) {
            tickEnd(event.player);
        }
    }

    public void tickEnd(EntityPlayer player) {
        ItemStack feetStack = player.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        if (!feetStack.isEmpty() && feetStack.getItem() instanceof ItemFreeRunners && !player.isSneaking()) {
            player.stepHeight = 1.002F;
        } else if (player.stepHeight == 1.002F) {
            player.stepHeight = 0.6F;
        }

        if (isFlamethrowerOn(player)) {
            player.world.spawnEntity(new EntityFlame(player));
            if (!player.isCreative() && !player.isSpectator()) {
                ItemStack currentItem = player.inventory.getCurrentItem();
                ((ItemFlamethrower) currentItem.getItem()).useGas(currentItem);
            }
        }

        if (isJetpackOn(player)) {
            ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            ItemJetpack jetpack = (ItemJetpack) stack.getItem();
            JetpackMode mode = jetpack.getMode(stack);
            if (mode == JetpackMode.NORMAL) {
                player.motionY = Math.min(player.motionY + 0.15D, 0.5D);
            } else if (mode == JetpackMode.HOVER) {
                boolean ascending = Mekanism.keyMap.has(player, KeySync.ASCEND);
                boolean descending = Mekanism.keyMap.has(player, KeySync.DESCEND);
                if ((!ascending && !descending) || (ascending && descending)) {
                    if (player.motionY > 0) {
                        player.motionY = Math.max(player.motionY - 0.15D, 0);
                    } else if (player.motionY < 0) {
                        if (!isOnGround(player)) {
                            player.motionY = Math.min(player.motionY + 0.15D, 0);
                        }
                    }
                } else if (ascending) {
                    player.motionY = Math.min(player.motionY + 0.15D, 0.2D);
                } else if (!isOnGround(player)) {
                    player.motionY = Math.max(player.motionY - 0.15D, -0.2D);
                }
            }
            player.fallDistance = 0.0F;
            if (player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) player).connection.floatingTickCount = 0;
            }
            jetpack.useGas(stack);
        }

        if (isGasMaskOn(player)) {
            ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            ItemScubaTank tank = (ItemScubaTank) stack.getItem();
            final int max = 300;
            tank.useGas(stack);
            GasStack received = tank.useGas(stack, max - player.getAir());
            if (received != null) {
                player.setAir(player.getAir() + received.amount);
            }
            if (player.getAir() == max) {
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    for (int i = 0; i < 9; i++) {
                        effect.onUpdate(player);
                    }
                }
            }
        }
    }

    public boolean isJetpackOn(EntityPlayer player) {
        if (!player.isCreative() && !player.isSpectator()) {
            ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            if (!chest.isEmpty() && chest.getItem() instanceof ItemJetpack jetpack) {
                if (jetpack.getGas(chest) != null) {
                    JetpackMode mode = jetpack.getMode(chest);
                    if (mode == JetpackMode.NORMAL) {
                        return Mekanism.keyMap.has(player, KeySync.ASCEND);
                    } else if (mode == JetpackMode.HOVER) {
                        boolean ascending = Mekanism.keyMap.has(player, KeySync.ASCEND);
                        boolean descending = Mekanism.keyMap.has(player, KeySync.DESCEND);
                        //if ((!ascending && !descending) || (ascending && descending) || descending)
                        //Simplifies to
                        if (!ascending || descending) {
                            return !isOnGround(player);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
