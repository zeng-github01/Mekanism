package mekanism.common.tile;

import io.netty.buffer.ByteBuf;
import java.util.List;
import javax.annotation.Nonnull;
import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.api.TileNetworkList;
import mekanism.api.gas.*;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.Mekanism;
import mekanism.common.SideData;
import mekanism.common.Upgrade;
import mekanism.common.Upgrade.IUpgradeInfoHandler;
import mekanism.common.base.*;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.config.MekanismConfig;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.GasInput;
import mekanism.common.recipe.machines.SolarNeutronRecipe;
import mekanism.common.security.ISecurityTile;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.tile.component.TileComponentSecurity;
import mekanism.common.tile.component.TileComponentUpgrade;
import mekanism.common.tile.prefab.TileEntityContainerBlock;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.ItemDataUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.TileUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntitySolarNeutronActivator extends TileEntityContainerBlock implements IRedstoneControl, IBoundingBlock, IGasHandler, IActiveState, ISustainedData,
        ITankManager, ISecurityTile, IUpgradeTile, IUpgradeInfoHandler, IComparatorSupport, ISideConfiguration {

    public static final int MAX_GAS = 10000;

    public GasTank inputTank = new GasTank(MAX_GAS);
    public GasTank outputTank = new GasTank(MAX_GAS);

    public RedstoneControl controlType = RedstoneControl.DISABLED;
    public TileComponentEjector ejectorComponent;
    public TileComponentConfig configComponent;
    public TileComponentUpgrade upgradeComponent = new TileComponentUpgrade(this, 2);
    public TileComponentSecurity securityComponent = new TileComponentSecurity(this);
    private SolarNeutronRecipe cachedRecipe;
    private int currentRedstoneLevel;
    private boolean isActive;
    private boolean needsRainCheck;

    public TileEntitySolarNeutronActivator() {
        super("SolarNeutronActivator");
        upgradeComponent.setSupported(Upgrade.ENERGY, false);

        configComponent = new TileComponentConfig(this, TransmissionType.ITEM, TransmissionType.GAS);
        configComponent.addOutput(TransmissionType.ITEM, new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
        configComponent.addOutput(TransmissionType.ITEM, new SideData("Input", EnumColor.RED, new int[]{0}));
        configComponent.addOutput(TransmissionType.ITEM, new SideData("Output", EnumColor.INDIGO, new int[]{1}));
        configComponent.setConfig(TransmissionType.ITEM, new byte[]{1, -1, 2, 1, 1, 1});
        configComponent.setCanEject(TransmissionType.ITEM, false);

        configComponent.addOutput(TransmissionType.GAS, new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
        configComponent.addOutput(TransmissionType.GAS, new SideData("Input", EnumColor.RED, new int[]{0}));
        configComponent.addOutput(TransmissionType.GAS, new SideData("Output", EnumColor.INDIGO, new int[]{1}));
        configComponent.setConfig(TransmissionType.GAS, new byte[]{1, -1, 2, 1, 1, 1});

        inventory = NonNullList.withSize(3, ItemStack.EMPTY);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(TransmissionType.GAS, configComponent.getOutputs(TransmissionType.GAS).get(2));
    }

    @Override
    public void validate() {
        super.validate();
        // Cache the flag to know if rain matters where this block is placed
        needsRainCheck = world.provider.getBiomeForCoords(getPos()).canRain();
    }

    @Override
    public void onUpdate() {
        if (!world.isRemote) {
            if (!inventory.get(0).isEmpty() && inventory.get(0).getItem() instanceof IGasItem && ((IGasItem) inventory.get(0).getItem()).getGas(inventory.get(0)) != null && RecipeHandler.Recipe.SOLAR_NEUTRON_ACTIVATOR.containsRecipe(((IGasItem) inventory.get(0).getItem()).getGas(inventory.get(0)).getGas())) {
                TileUtils.receiveGas(inventory.get(0), inputTank);
            }
            TileUtils.drawGas(inventory.get(1), outputTank);
            SolarNeutronRecipe recipe = getRecipe();

            // TODO: Ideally the neutron activator should use the sky brightness to determine throughput; but
            // changing this would dramatically affect a lot of setups with Fusion reactors which can take
            // a long time to relight. I don't want to be chased by a mob right now, so just doing basic
            // rain checks.
            boolean seesSun = world.isDaytime() && world.canSeeSky(getPos().up()) && !world.provider.isNether();
            if (needsRainCheck) {
                seesSun &= !(world.isRaining() || world.isThundering());
            }

            if (seesSun && canOperate(recipe) && MekanismUtils.canFunction(this)) {
                setActive(true);
                operate(recipe);
            } else {
                setActive(false);
            }

            // Every 20 ticks (once a second), send update to client. Note that this is a 50% reduction in network
            // traffic from previous implementation that send the update every 10 ticks.
            if (world.getTotalWorldTime() % 20 == 0) {
                Mekanism.packetHandler.sendUpdatePacket(this);
            }

            int newRedstoneLevel = getRedstoneLevel();
            if (newRedstoneLevel != currentRedstoneLevel) {
                world.updateComparatorOutputLevel(pos, getBlockType());
                currentRedstoneLevel = newRedstoneLevel;
            }
        }
    }

    public int getUpgradedUsage(SolarNeutronRecipe recipe) {
        int possibleProcess = Math.min((int) Math.pow(2, upgradeComponent.getUpgrades(Upgrade.SPEED)), MekanismConfig.current().mekce.MAXspeedmachines.val());
        possibleProcess = Math.min(Math.min(inputTank.getStored(), outputTank.getNeeded()), possibleProcess);
        return Math.min(inputTank.getStored() / recipe.recipeInput.ingredient.amount, possibleProcess);
    }

    public SolarNeutronRecipe getRecipe() {
        GasInput input = getInput();
        if (cachedRecipe == null || !input.testEquality(cachedRecipe.getInput())) {
            cachedRecipe = RecipeHandler.getSolarNeutronRecipe(getInput());
        }
        return cachedRecipe;
    }

    public GasInput getInput() {
        return new GasInput(inputTank.getGas());
    }

    public boolean canOperate(SolarNeutronRecipe recipe) {
        return recipe != null && recipe.canOperate(inputTank, outputTank);
    }

    public void operate(SolarNeutronRecipe recipe) {
        recipe.operate(inputTank, outputTank, getUpgradedUsage(recipe));
    }

    @Override
    public void handlePacketData(ByteBuf dataStream) {
        super.handlePacketData(dataStream);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            isActive = dataStream.readBoolean();
            controlType = RedstoneControl.values()[dataStream.readInt()];
            TileUtils.readTankData(dataStream, inputTank);
            TileUtils.readTankData(dataStream, outputTank);
        }
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(isActive);
        data.add(controlType.ordinal());
        TileUtils.addTankData(data, inputTank);
        TileUtils.addTankData(data, outputTank);
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTags) {
        super.readFromNBT(nbtTags);
        isActive = nbtTags.getBoolean("isActive");
        controlType = RedstoneControl.values()[nbtTags.getInteger("controlType")];
        inputTank.read(nbtTags.getCompoundTag("inputTank"));
        outputTank.read(nbtTags.getCompoundTag("outputTank"));
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbtTags) {
        super.writeToNBT(nbtTags);
        nbtTags.setBoolean("isActive", isActive);
        nbtTags.setInteger("controlType", controlType.ordinal());
        nbtTags.setTag("inputTank", inputTank.write(new NBTTagCompound()));
        nbtTags.setTag("outputTank", outputTank.write(new NBTTagCompound()));
        return nbtTags;
    }

    @Override
    public boolean canSetFacing(@Nonnull EnumFacing facing) {
        return facing != EnumFacing.DOWN && facing != EnumFacing.UP;
    }

    @Override
    public void onPlace() {
        MekanismUtils.makeBoundingBlock(world, Coord4D.get(this).offset(EnumFacing.UP).getPos(), Coord4D.get(this));
    }

    @Override
    public void onBreak() {
        world.setBlockToAir(getPos().up());
        world.setBlockToAir(getPos());
    }

    @Override
    public int receiveGas(EnumFacing side, GasStack stack, boolean doTransfer) {
        if (canReceiveGas(side, stack.getGas())) {
            int recipeAmount = RecipeHandler.Recipe.SOLAR_NEUTRON_ACTIVATOR.get().get(new GasInput(stack)).recipeInput.ingredient.amount;
            int receivable = inputTank.receive(stack, false);
            int stored = inputTank.stored != null ? inputTank.stored.amount : 0;
            int newStored = stored + receivable;
            int amount = newStored - stored - newStored % recipeAmount;
            return inputTank.receive(stack.copy().withAmount(amount), doTransfer);
        }
        return 0;
    }

    @Override
    public GasStack drawGas(EnumFacing side, int amount, boolean doTransfer) {
        if (canDrawGas(side, null)) {
            return outputTank.draw(amount, doTransfer);
        }
        return null;
    }

    @Override
    public boolean canReceiveGas(EnumFacing side, Gas type) {
        return configComponent.getOutput(TransmissionType.GAS, side, facing).hasSlot(0) && inputTank.canReceive(type) && RecipeHandler.Recipe.SOLAR_NEUTRON_ACTIVATOR.containsRecipe(type);
    }

    @Override
    public boolean canDrawGas(EnumFacing side, Gas type) {
        return configComponent.getOutput(TransmissionType.GAS, side, facing).hasSlot(1) && outputTank.canDraw(type);
    }

    @Nonnull
    @Override
    public GasTankInfo[] getTankInfo() {
        return new GasTankInfo[]{inputTank, outputTank};
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing side) {
        if (isCapabilityDisabled(capability, side)) {
            return false;
        }
        return capability == Capabilities.GAS_HANDLER_CAPABILITY || super.hasCapability(capability, side);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing side) {
        if (isCapabilityDisabled(capability, side)) {
            return null;
        } else if (capability == Capabilities.GAS_HANDLER_CAPABILITY) {
            return Capabilities.GAS_HANDLER_CAPABILITY.cast(this);
        }
        return super.getCapability(capability, side);
    }

    @Override
    public boolean isCapabilityDisabled(@Nonnull Capability<?> capability, EnumFacing side) {
        return configComponent.isCapabilityDisabled(capability, side, facing) || super.isCapabilityDisabled(capability, side);
    }

    @Override
    public void writeSustainedData(ItemStack itemStack) {
        if (inputTank.getGas() != null) {
            ItemDataUtils.setCompound(itemStack, "inputTank", inputTank.getGas().write(new NBTTagCompound()));
        }
        if (outputTank.getGas() != null) {
            ItemDataUtils.setCompound(itemStack, "outputTank", outputTank.getGas().write(new NBTTagCompound()));
        }
    }

    @Override
    public void readSustainedData(ItemStack itemStack) {
        inputTank.setGas(GasStack.readFromNBT(ItemDataUtils.getCompound(itemStack, "inputTank")));
        outputTank.setGas(GasStack.readFromNBT(ItemDataUtils.getCompound(itemStack, "outputTank")));
    }

    @Override
    public RedstoneControl getControlType() {
        return controlType;
    }

    @Override
    public void setControlType(RedstoneControl type) {
        controlType = type;
        MekanismUtils.saveChunk(this);
    }

    @Override
    public boolean canPulse() {
        return false;
    }

    @Override
    public boolean getActive() {
        return isActive;
    }

    @Override
    public void setActive(boolean active) {
        boolean stateChange = isActive != active;
        if (stateChange) {
            isActive = active;
            Mekanism.packetHandler.sendUpdatePacket(this);
        }
    }

    @Override
    public boolean renderUpdate() {
        return false;
    }

    @Override
    public boolean lightUpdate() {
        return false;
    }

    @Override
    public Object[] getTanks() {
        return new Object[]{inputTank, outputTank};
    }

    @Override
    public TileComponentSecurity getSecurity() {
        return securityComponent;
    }

    @Override
    public TileComponentUpgrade getComponent() {
        return upgradeComponent;
    }

    @Override
    public List<String> getInfo(Upgrade upgrade) {
        return upgrade == Upgrade.SPEED ? upgrade.getExpScaledInfo(this) : upgrade.getMultScaledInfo(this);
    }

    @Nonnull
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }


    @Nonnull
    @Override
    public int[] getSlotsForFace(@Nonnull EnumFacing side) {
        return configComponent.getOutput(TransmissionType.ITEM, side, facing).availableSlots;
    }

    @Override
    public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
        return stack.getItem() instanceof IGasItem;
    }

    @Override
    public int getRedstoneLevel() {
        return MekanismUtils.redstoneLevelFromContents(inputTank.getStored(), inputTank.getMaxGas());
    }

    @Override
    public TileComponentConfig getConfig() {
        return configComponent;
    }

    @Override
    public EnumFacing getOrientation() {
        return facing;
    }

    @Override
    public TileComponentEjector getEjector() {
        return ejectorComponent;
    }
}
