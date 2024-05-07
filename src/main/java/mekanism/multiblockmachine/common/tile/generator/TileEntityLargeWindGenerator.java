package mekanism.multiblockmachine.common.tile.generator;

import io.netty.buffer.ByteBuf;
import mekanism.api.Coord4D;
import mekanism.api.TileNetworkList;
import mekanism.common.base.IBoundingBlock;
import mekanism.common.config.MekanismConfig;
import mekanism.common.util.ChargeUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

public class TileEntityLargeWindGenerator extends TileEntityMultiblockGenerator implements IBoundingBlock {

    public static final float SPEED = 32F;
    public static final float SPEED_SCALED = 256F / SPEED;
    static final String[] methods = new String[]{"getEnergy", "getOutput", "getMaxEnergy", "getEnergyNeeded", "getMultiplier"};
    private static final int[] SLOTS = {0};
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
        }
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(currentMultiplier);
        data.add(isBlacklistDimension);
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
        Coord4D current = Coord4D.get(this);
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                MekanismUtils.makeBoundingBlock(world, getPos().add(x, 0, z), current);
            }
        }
        for (int y = 1; y < 50; y++) {
            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    MekanismUtils.makeBoundingBlock(world, getPos().add(x, y, z), current);
                }
            }
        }

        // Check to see if the placement is happening in a blacklisted dimension
        isBlacklistDimension = MekanismConfig.current().generators.windGenerationDimBlacklist.val().contains(world.provider.getDimension());
    }


    @Override
    public void onBreak() {
        for (int y = 1; y < 50; y++) {
            for (int x = -3; x <= 3; x++) {
                for (int z = -3; z <= 3; z++) {
                    world.setBlockToAir(getPos().add(x, y, z));
                }
            }
        }
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                world.setBlockToAir(getPos().add(x, 0, z));
            }
        }
        invalidate();
        world.setBlockToAir(getPos());
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
}
