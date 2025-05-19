package mekanism.common.lib.radiation;

import mekanism.api.NBTConstants;
import mekanism.common.util.WorldUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;
import java.util.UUID;

public class Meltdown {

    private static final int DURATION = 100;

    private final BlockPos minPos, maxPos;
    private final double magnitude, chance;
    private final UUID multiblockID;

    private int ticksExisted;

    public Meltdown(BlockPos minPos, BlockPos maxPos, double magnitude, double chance, UUID multiblockID) {
        this(minPos, maxPos, magnitude, chance, multiblockID, 0);
    }

    private Meltdown(BlockPos minPos, BlockPos maxPos, double magnitude, double chance, UUID multiblockID, int ticksExisted) {
        this.minPos = minPos;
        this.maxPos = maxPos;
        this.magnitude = magnitude;
        this.chance = chance;
        this.multiblockID = multiblockID;
        this.ticksExisted = ticksExisted;
    }

    public static Meltdown load(NBTTagCompound tag) {
        return new Meltdown(
                NBTUtil.getPosFromTag(tag.getCompoundTag(NBTConstants.MIN)),
                NBTUtil.getPosFromTag(tag.getCompoundTag(NBTConstants.MAX)),
                tag.getDouble(NBTConstants.MAGNITUDE),
                tag.getDouble(NBTConstants.CHANCE),
                tag.getUniqueId(NBTConstants.INVENTORY_ID),
                tag.getInteger(NBTConstants.AGE)
        );
    }

    public void write(NBTTagCompound tag) {
        tag.setTag(NBTConstants.MIN, NBTUtil.createPosTag(minPos));
        tag.setTag(NBTConstants.MAX, NBTUtil.createPosTag(maxPos));
        tag.setDouble(NBTConstants.MAGNITUDE, magnitude);
        tag.setDouble(NBTConstants.CHANCE, chance);
        tag.setUniqueId(NBTConstants.INVENTORY_ID, multiblockID);
        tag.setInteger(NBTConstants.AGE, ticksExisted);
    }

    public boolean update(World world) {
        ticksExisted++;

        if (world.rand.nextInt() % 10 == 0 && world.rand.nextDouble() < magnitude * chance) {
            int x = nextInt(world.rand, minPos.getX(), maxPos.getX());
            int y = nextInt(world.rand, minPos.getY(), maxPos.getY());
            int z = nextInt(world.rand, minPos.getZ(), maxPos.getZ());
            world.newExplosion(null, x, y, z, 8, true, true);
        }

        if (!WorldUtils.isBlockLoaded(world, minPos) || !WorldUtils.isBlockLoaded(world, maxPos)) {
            return true;
        }

        return ticksExisted >= DURATION;
    }


    public static int nextInt(Random pRandom, int pMinimum, int pMaximum) {
        return pMinimum >= pMaximum ? pMinimum : pRandom.nextInt(pMaximum - pMinimum + 1) + pMinimum;
    }
}
