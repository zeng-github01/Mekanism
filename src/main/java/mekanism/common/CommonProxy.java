package mekanism.common;

import mekanism.api.Coord4D;
import mekanism.api.MekanismAPI;
import mekanism.api.Pos3D;
import mekanism.client.SparkleAnimation.INodeChecker;
import mekanism.common.base.IGuiProvider;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.block.states.BlockStateMachine.MachineType;
import mekanism.common.config.MekanismConfig;
import mekanism.common.entity.EntityRobit;
import mekanism.common.inventory.InventoryPersonalChest;
import mekanism.common.inventory.container.*;
import mekanism.common.inventory.container.robit.*;
import mekanism.common.item.ItemDictionary;
import mekanism.common.item.ItemPortableTeleporter;
import mekanism.common.item.ItemSeismicReader;
import mekanism.common.network.PacketPortableTeleporter.PortableTeleporterMessage;
import mekanism.common.tile.*;
import mekanism.common.tile.factory.TileEntityFactory;
import mekanism.common.tile.laser.*;
import mekanism.common.tile.machine.*;
import mekanism.common.tile.multiblock.TileEntityDynamicTank;
import mekanism.common.tile.multiblock.TileEntityInductionCasing;
import mekanism.common.tile.multiblock.TileEntityThermalEvaporationController;
import mekanism.common.tile.prefab.*;
import mekanism.common.voice.VoiceServerManager;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.oredict.OreDictionary;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Common proxy for the Mekanism mod.
 *
 * @author AidanBrady
 */
public class CommonProxy implements IGuiProvider {

    protected final String[] API_PRESENT_MESSAGE = {"Mekanism API jar detected (Mekanism-<version>-api.jar),",
            "please delete it from your mods folder and restart the game."};

    /**
     * Register tile entities that have special models. Overwritten in client to register TESRs.
     */
    public void registerTESRs() {
    }

    public void handleTeleporterUpdate(PortableTeleporterMessage message) {
    }

    /**
     * Register and load client-only item render information.
     */
    public void registerItemRenders() {
    }

    /**
     * Register and load client-only block render information.
     */
    public void registerBlockRenders() {
    }

    /**
     * Set and load the mod's common configuration properties.
     */
    public void loadConfiguration() {
        MekanismConfig.local().general.load(Mekanism.configuration);
        MekanismConfig.local().mekce.load(Mekanism.configurationce);
        MekanismConfig.local().usage.load(Mekanism.configuration);
        MekanismConfig.local().storage.load(Mekanism.configuration);
        if (Mekanism.configuration.hasChanged()) {
            Mekanism.configuration.save();
        }
        if (Mekanism.configurationce.hasChanged()) {
            Mekanism.configurationce.save();
        }
    }

    /**
     * Set up and load the utilities this mod uses.
     */
    public void init() {
        MinecraftForge.EVENT_BUS.register(Mekanism.worldTickHandler);
    }

    /**
     * Whether or not the game is paused.
     */
    public boolean isPaused() {
        return false;
    }

    /**
     * Adds block hit effects on the client side.
     */
    public void addHitEffects(Coord4D coord, RayTraceResult mop) {
    }

    /**
     * Does the multiblock creation animation, starting from the rendering block.
     */
    public void doMultiblockSparkle(TileEntity tileEntity, BlockPos corner1, BlockPos corner2, INodeChecker checker) {
    }

    /**
     * Does the multiblock creation animation, starting from the rendering block.
     */
    public void doMultiblockSparkle(TileEntity tileEntity, BlockPos renderLoc, int length, int width, int height, INodeChecker checker) {
    }

    @Override
    public Object getClientGui(int ID, EntityPlayer player, World world, BlockPos pos) {
        return null;
    }

    private Container getServerItemGui(EntityPlayer player, BlockPos pos) {
        int currentItem = pos.getX();
        int handOrdinal = pos.getY();
        if (currentItem < 0 || currentItem >= player.inventory.mainInventory.size() || handOrdinal < 0 || handOrdinal >= EnumHand.values().length) {
            //If it is out of bounds don't do anything
            return null;
        }
        ItemStack stack = player.inventory.getStackInSlot(currentItem);
        if (stack.isEmpty()) {
            return null;
        }
        EnumHand hand = EnumHand.values()[handOrdinal];
        int guiID = pos.getZ();
        switch (guiID) {
            case 0:
                if (stack.getItem() instanceof ItemDictionary) {
                    return new ContainerDictionary(player.inventory);
                }
                break;
            case 14:
                if (stack.getItem() instanceof ItemPortableTeleporter) {
                    return new ContainerNull();
                }
            case 19:
                if (MachineType.get(stack) == MachineType.PERSONAL_CHEST) {
                    //Ensure the item didn't change. From testing even if it did things still seemed to work properly but better safe than sorry
                    return new ContainerPersonalChest(player.inventory, new InventoryPersonalChest(stack, hand));
                }
                break;
            case 38:
                if (stack.getItem() instanceof ItemSeismicReader) {
                    return new ContainerNull();
                }
                break;
        }
        return null;
    }

    private Container getServerEntityGui(EntityPlayer player, World world, BlockPos pos) {
        int entityID = pos.getX();
        Entity entity = world.getEntityByID(entityID);
        if (entity == null) {
            return null;
        }
        int guiID = pos.getY();
        switch (guiID) {
            case 21 -> {
                if (entity instanceof EntityRobit) {
                    return new ContainerRobitMain(player.inventory, (EntityRobit) entity);
                }
            }
            case 22 -> {
                if (entity instanceof EntityRobit) {
                    return new ContainerRobitCrafting(player.inventory, (EntityRobit) entity);
                }
            }
            case 23 -> {
                if (entity instanceof EntityRobit) {
                    return new ContainerRobitInventory(player.inventory, (EntityRobit) entity);
                }
            }
            case 24 -> {
                if (entity instanceof EntityRobit) {
                    return new ContainerRobitSmelting(player.inventory, (EntityRobit) entity);
                }
            }
            case 25 -> {
                if (entity instanceof EntityRobit) {
                    return new ContainerRobitRepair(player.inventory, (EntityRobit) entity);
                }
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Container getServerGui(int ID, EntityPlayer player, World world, BlockPos pos) {
        //TODO: Replace magic numbers here and in sub methods with static lookup ints
        if (ID == 0) {
            return getServerItemGui(player, pos);
        } else if (ID == 1) {
            return getServerEntityGui(player, world, pos);
        }
        TileEntity tileEntity = world.getTileEntity(pos);
        return switch (ID) {
            //0, 1 USED BEFORE SWITCH
            case 2 -> new ContainerDigitalMiner(player.inventory, (TileEntityDigitalMiner) tileEntity);
            case 3, 6, 16, 64, 65, 66, 67 ->
                    new ContainerElectricMachine<>(player.inventory, (TileEntityElectricMachine) tileEntity);
            case 4, 31 ->
                    new ContainerAdvancedElectricMachine<>(player.inventory, (TileEntityAdvancedElectricMachine) tileEntity);
            case 5, 68 ->
                    new ContainerDoubleElectricMachine<>(player.inventory, (TileEntityDoubleElectricMachine) tileEntity);
            case 7 ->
                    new ContainerRotaryCondensentrator(player.inventory, (TileEntityRotaryCondensentrator) tileEntity);
            case 8 -> new ContainerEnergyCube(player.inventory, (TileEntityEnergyCube) tileEntity);
            case 9, 50, 51, 55, 59 -> new ContainerNull(player, (TileEntityContainerBlock) tileEntity);
            case 10 -> new ContainerGasTank(player.inventory, (TileEntityGasTank) tileEntity);
            case 11 -> new ContainerFactory(player.inventory, (TileEntityFactory) tileEntity);
            case 12 -> new ContainerMetallurgicInfuser(player.inventory, (TileEntityMetallurgicInfuser) tileEntity);
            case 13 -> new ContainerTeleporter(player.inventory, (TileEntityTeleporter) tileEntity);
            //EMPTY 14
            case 15 ->
                    new ContainerAdvancedElectricMachine<>(player.inventory, (TileEntityAdvancedElectricMachine) tileEntity);
            case 17 -> new ContainerElectricPump(player.inventory, (TileEntityElectricPump) tileEntity);
            case 18 -> new ContainerDynamicTank(player.inventory, (TileEntityDynamicTank) tileEntity);
            case 19 -> new ContainerPersonalChest(player.inventory, (TileEntityPersonalChest) tileEntity);
            //EMPTY 20, 21, 22, 23, 24, 25
            case 26 -> new ContainerNull(player, (TileEntityContainerBlock) tileEntity);
            case 27, 28 -> new ContainerFilter(player.inventory, (TileEntityContainerBlock) tileEntity);
            case 29 -> new ContainerChemicalOxidizer(player.inventory, (TileEntityChemicalOxidizer) tileEntity);
            case 30 -> new ContainerChemicalInfuser(player.inventory, (TileEntityChemicalInfuser) tileEntity);
            case 32 ->
                    new ContainerElectrolyticSeparator(player.inventory, (TileEntityElectrolyticSeparator) tileEntity);
            case 33 ->
                    new ContainerThermalEvaporationController(player.inventory, (TileEntityThermalEvaporationController) tileEntity);
            case 34, 71 -> new ContainerChanceMachine<>(player.inventory, (TileEntityChanceMachine) tileEntity);
            case 35 ->
                    new ContainerChemicalDissolutionChamber(player.inventory, (TileEntityChemicalDissolutionChamber) tileEntity);
            case 36 -> new ContainerChemicalWasher(player.inventory, (TileEntityChemicalWasher) tileEntity);
            case 37 -> new ContainerChemicalCrystallizer(player.inventory, (TileEntityChemicalCrystallizer) tileEntity);
            //EMPTY 38
            case 39 -> new ContainerSeismicVibrator(player.inventory, (TileEntitySeismicVibrator) tileEntity);
            case 40 -> new ContainerPRC(player.inventory, (TileEntityPRC) tileEntity);
            case 41 -> new ContainerFluidTank(player.inventory, (TileEntityFluidTank) tileEntity);
            case 42 -> new ContainerFluidicPlenisher(player.inventory, (TileEntityFluidicPlenisher) tileEntity);
            case 43 -> new ContainerUpgradeManagement(player.inventory, (IUpgradeTile) tileEntity);
            case 44 -> new ContainerLaserAmplifier(player.inventory, (TileEntityLaserAmplifier) tileEntity);
            case 45 -> new ContainerLaserTractorBeam(player.inventory, (TileEntityLaserTractorBeam) tileEntity);
            case 46 -> new ContainerQuantumEntangloporter(player.inventory, (TileEntityQuantumEntangloporter) tileEntity);
            case 47 -> new ContainerSolarNeutronActivator(player.inventory, (TileEntitySolarNeutronActivator) tileEntity);
            case 48 -> new ContainerAmbientAccumulator(player.inventory,(TileEntityAmbientAccumulator) tileEntity);
            case 49 -> new ContainerInductionMatrix(player.inventory, (TileEntityInductionCasing) tileEntity);
            case 52 -> new ContainerOredictionificator(player.inventory, (TileEntityOredictionificator) tileEntity);
            case 53 -> new ContainerResistiveHeater(player.inventory, (TileEntityResistiveHeater) tileEntity);
            case 54 -> new ContainerThermoelectricBoiler(player.inventory, (TileEntityContainerBlock) tileEntity);
            case 56 -> new ContainerFormulaicAssemblicator(player.inventory, (TileEntityFormulaicAssemblicator) tileEntity);
            case 57 -> new ContainerSecurityDesk(player.inventory, (TileEntitySecurityDesk) tileEntity);
            case 58 -> new ContainerFuelwoodHeater(player.inventory, (TileEntityFuelwoodHeater) tileEntity);
            case 60 -> new ContainerIsotopicCentrifuge(player.inventory, (TileEntityIsotopicCentrifuge) tileEntity);
            case 61 -> new ContainerNutritionalLiquifier(player.inventory, (TileEntityNutritionalLiquifier) tileEntity);
            case 62 -> new ContainerFarmMachine(player.inventory, (TileEntityFarmMachine) tileEntity);
            case 63 -> new ContainerAntiprotonicNucleosynthesizer(player.inventory, (TileEntityAntiprotonicNucleosynthesizer) tileEntity);
            /*
            case 69:
                return new ContainerCultivateElectricMachine<>(player.inventory, (TileEntityCultivateElectricMachine) tileEntity);
            */
            case 70 -> new ContainerChanceMachine<>(player.inventory, (TileEntityChanceMachine) tileEntity);
            case 72 -> new ContainerChanceMachine2(player.inventory, (TileEntityChanceMachine2) tileEntity);
            case 73 -> new ContainerAmbientAccumulatorEnergy(player.inventory, (TileEntityAmbientAccumulatorEnergy) tileEntity);
            case 74 -> new ContainerDimensionalStabilizer((TileEntityDimensionalStabilizer) tileEntity, player.inventory);
            default -> null;
        };
    }

    public void preInit() {

    }

    public double getReach(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            return player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        }
        return 0;
    }

    /**
     * Gets the Minecraft base directory.
     *
     * @return base directory
     */
    public File getMinecraftDir() {
        return (File) FMLInjectionData.data()[6];
    }

    public void onConfigSync(boolean fromPacket) {
        if (MekanismConfig.current().general.cardboardSpawners.val()) {
            if (Blocks.MOB_SPAWNER != null) { //There may be mods that will remove this and cause a crash
                MekanismAPI.removeBoxBlacklist(Blocks.MOB_SPAWNER, OreDictionary.WILDCARD_VALUE);
            }
        } else {
            MekanismAPI.addBoxBlacklist(Blocks.MOB_SPAWNER, OreDictionary.WILDCARD_VALUE);
        }
        if (MekanismConfig.current().general.voiceServerEnabled.val() && Mekanism.voiceManager == null) {
            Mekanism.voiceManager = new VoiceServerManager();
        }
        if (fromPacket) {
            Mekanism.logger.info("Received config from server.");
        }
    }

    public final WeakReference<EntityPlayer> getDummyPlayer(WorldServer world) {
        return MekFakePlayer.getInstance(world);
    }

    public final WeakReference<EntityPlayer> getDummyPlayer(WorldServer world, double x, double y, double z) {
        return MekFakePlayer.getInstance(world, x, y, z);
    }

    public final WeakReference<EntityPlayer> getDummyPlayer(WorldServer world, BlockPos pos) {
        return getDummyPlayer(world, pos.getX(), pos.getY(), pos.getZ());
    }

    public EntityPlayer getPlayer(MessageContext context) {
        return context.getServerHandler().player;
    }

    public void handlePacket(Runnable runnable, EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            ((WorldServer) player.world).addScheduledTask(runnable);
        }
    }

    public int getGuiId(Block block, int metadata) {
        if (MachineType.get(block, metadata) != null) {
            return MachineType.get(block, metadata).guiId;
        } else if (block == MekanismBlocks.GasTank) {
            return 10;
        } else if (block == MekanismBlocks.EnergyCube) {
            return 8;
        }
        return -1;
    }

    public void renderLaser(World world, Pos3D from, Pos3D to, EnumFacing direction, double energy) {
    }

    public Object getFontRenderer() {
        return null;
    }

    public void throwApiPresentException() {
        throw new RuntimeException(String.join(" ", API_PRESENT_MESSAGE));
    }
}
