package mekanism.multiblockmachine.common.tile.generator;

import io.netty.buffer.ByteBuf;
import mekanism.api.Coord4D;
import mekanism.api.TileNetworkList;
import mekanism.common.base.IAdvancedBoundingBlock;
import mekanism.common.config.MekanismConfig;
import mekanism.common.util.ChargeUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class TileEntityLargeWindGenerator extends TileEntityMultiblockGenerator implements IAdvancedBoundingBlock {

    public static final float SPEED = 32F;
    public static final float SPEED_SCALED = 256F / SPEED;
    static final String[] methods = new String[]{"getEnergy", "getOutput", "getMaxEnergy", "getEnergyNeeded", "getMultiplier"};
    private static final int[] SLOTS = {0};
    public int numPowering;
    private double angle;
    private float currentMultiplier;
    private boolean isBlacklistDimension = false;

    public TileEntityLargeWindGenerator() {
        super("wind", "LargeWindGenerator", MekanismConfig.current().multiblock.largewindGeneratorStorage.val(), MekanismConfig.current().multiblock.largewindGenerationMax.val() * 2);
        inventory = NonNullList.withSize(SLOTS.length, ItemStack.EMPTY);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        // Check the blacklist and force an update if we're in the blacklist. Otherwise, we'll never send
        // an initial activity status and the client (in MP) will show the windmills turning while not
        // generating any power
        isBlacklistDimension = MekanismConfig.current().generators.windGenerationDimBlacklist.val().contains(world.provider.getDimension());
        if (isBlacklistDimension) {
            setActive(false);
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!world.isRemote) {
            ChargeUtils.charge(0, this);
            // If we're in a blacklisted dimension, there's nothing more to do
            if (isBlacklistDimension) {
                return;
            }

            if (ticker % 20 == 0) {
                currentMultiplier = getMultiplier();
                setActive(MekanismUtils.canFunction(this) && currentMultiplier > 0);
            }
            if (getActive()) {
                setEnergy(electricityStored + (MekanismConfig.current().multiblock.largewindGenerationMin.val() * currentMultiplier));
            }
        } else if (getActive()) {
            angle = (angle + (getPos().getY() + 4F) / SPEED_SCALED) % 360;
        }
    }

    @Override
    public void handlePacketData(ByteBuf dataStream) {
        super.handlePacketData(dataStream);
        if (world.isRemote) {
            currentMultiplier = dataStream.readFloat();
            isBlacklistDimension = dataStream.readBoolean();
            numPowering = dataStream.readInt();
        }
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(currentMultiplier);
        data.add(isBlacklistDimension);
        data.add(numPowering);
        return data;
    }

    public float getMultiplier() {
        BlockPos top = getPos().up(49);
        if (world.canSeeSky(top)) {
            int minY = MekanismConfig.current().multiblock.largewindGenerationMinY.val();
            int maxY = MekanismConfig.current().multiblock.largewindGenerationMaxY.val();
            float clampedY = Math.min(maxY, Math.max(minY, top.getY()));
            float minG = (float) MekanismConfig.current().multiblock.largewindGenerationMin.val();
            float maxG = (float) MekanismConfig.current().multiblock.largewindGenerationMax.val();
            int rangeY = maxY < minY ? minY - maxY : maxY - minY;
            float rangG = maxG < minG ? minG - maxG : maxG - minG;
            float slope = rangG / rangeY;
            float toGen = minG + (slope * (clampedY - minY));
            return toGen / minG;
        }
        return 0;
    }

    @Override
    public String[] getMethods() {
        return methods;
    }


    @Override
    public Object[] invoke(int method, Object[] arguments) throws NoSuchMethodException {
        return switch (method) {
            case 0 -> new Object[]{electricityStored};
            case 1 -> new Object[]{output};
            case 2 -> new Object[]{BASE_MAX_ENERGY};
            case 3 -> new Object[]{BASE_MAX_ENERGY - electricityStored};
            case 4 -> new Object[]{getMultiplier()};
            default -> throw new NoSuchMethodException();
        };
    }

    @Override
    public boolean canOperate() {
        return electricityStored < BASE_MAX_ENERGY && getMultiplier() > 0 && MekanismUtils.canFunction(this);
    }

    @Override
    public void onPlace() {
        for (int y = 0; y < 50; y++) {
            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }
                    BlockPos pos1 = getPos().add(x, y, z);
                    MekanismUtils.makeAdvancedBoundingBlock(world, pos1, Coord4D.get(this));
                }
            }
        }

        // Check to see if the placement is happening in a blacklisted dimension
        isBlacklistDimension = MekanismConfig.current().generators.windGenerationDimBlacklist.val().contains(world.provider.getDimension());
    }

    @Override
    public boolean sideIsOutput(EnumFacing side) {
        return side == MekanismUtils.getLeft(facing) || side == MekanismUtils.getRight(facing) || side == facing;
    }

    @Override
    public void onBreak() {
        for (int y = 0; y < 50; y++) {
            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }
                    world.setBlockToAir(getPos().add(x, y, z));
                }
            }
        }
        invalidate();
        world.setBlockToAir(getPos());
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbtTags) {
        super.readCustomNBT(nbtTags);
        numPowering = nbtTags.getInteger("numPowering");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbtTags) {
        super.writeCustomNBT(nbtTags);
        nbtTags.setInteger("numPowering", numPowering);
    }


    @Override
    public boolean renderUpdate() {
        return false;
    }

    @Override
    public boolean lightUpdate() {
        return false;
    }

    public float getCurrentMultiplier() {
        return currentMultiplier;
    }

    public double getAngle() {
        return angle;
    }

    public boolean isBlacklistDimension() {
        return isBlacklistDimension;
    }

    @Nonnull
    @Override
    public int[] getSlotsForFace(@Nonnull EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
        return ChargeUtils.canBeCharged(stack);
    }

    @Override
    public boolean canBoundReceiveEnergy(BlockPos location, EnumFacing side) {
        return false;
    }

    @Override
    public boolean canBoundOutPutEnergy(BlockPos coord, EnumFacing side) {
        EnumFacing left = MekanismUtils.getLeft(facing);
        EnumFacing right = MekanismUtils.getRight(facing);
        if (coord.equals(getPos().offset(left,3))) {
            return side == left;
        } else if (coord.equals(getPos().offset(right,3))) {
            return side == right;
        }
        return false;
    }

    @Override
    public void onPower() {
        numPowering++;
    }

    @Override
    public void onNoPower() {
        numPowering--;
    }

    @Override
    public boolean isPowered() {
        return redstone || numPowering > 0;
    }

    @Override
    public NBTTagCompound getConfigurationData(NBTTagCompound nbtTags) {
        return nbtTags;
    }

    @Override
    public void setConfigurationData(NBTTagCompound nbtTags) {

    }

    @Override
    public String getDataType() {
        return getBlockType().getTranslationKey() + "." + fullName + ".name";
    }

    @Override
    public boolean hasOffsetCapability(@NotNull Capability<?> capability, @Nullable EnumFacing side, @NotNull Vec3i offset) {
        if (isOffsetCapabilityDisabled(capability, side, offset)) {
            return false;
        }
        if (isStrictEnergy(capability) || capability == CapabilityEnergy.ENERGY || isTesla(capability, side)) {
            return true;
        }
        return hasCapability(capability, side);
    }

    @Override
    public <T> T getOffsetCapability(@Nonnull Capability<T> capability, EnumFacing side, @Nonnull Vec3i offset) {
        if (isOffsetCapabilityDisabled(capability, side, offset)) {
            return null;
        } else if (isStrictEnergy(capability)) {
            return (T) this;
        } else if (isTesla(capability, side)) {
            return (T) getTeslaEnergyWrapper(side);
        } else if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(getForgeEnergyWrapper(side));
        }
        return getCapability(capability, side);
    }

    @Override
    public boolean isOffsetCapabilityDisabled(@Nonnull Capability<?> capability, EnumFacing side, @Nonnull Vec3i offset) {
        if (isStrictEnergy(capability) || capability == CapabilityEnergy.ENERGY || isTesla(capability, side)) {
            EnumFacing left = MekanismUtils.getLeft(facing);
            EnumFacing right = MekanismUtils.getRight(facing);
            if (offset.equals(new Vec3i(left.getXOffset() * 4, 0, left.getZOffset() * 4))) {
                //Disable if left power port but wrong side of the port
                return side != left;
            } else if (offset.equals(new Vec3i(right.getXOffset() * 4, 0, right.getZOffset() * 4))) {
                //Disable if right power port but wrong side of the port
                return side != right;
            }else if (offset.equals(new Vec3i(facing.getXOffset() * 4, 0, facing.getZOffset() * 4))) {
                //Disable if right power port but wrong side of the port
                return side != facing;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isCapabilityDisabled(@Nonnull Capability<?> capability, EnumFacing side) {
        if (isStrictEnergy(capability) || capability == CapabilityEnergy.ENERGY || isTesla(capability, side)) {
            return true;
        }
        return super.isCapabilityDisabled(capability, side);
    }

}
