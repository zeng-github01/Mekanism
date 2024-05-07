package mekanism.multiblockmachine.common;

import mekanism.common.Mekanism;
import mekanism.common.base.IGuiProvider;
import mekanism.common.config.MekanismConfig;
import mekanism.multiblockmachine.common.inventory.container.ContainerLargeWindGenerator;
import mekanism.multiblockmachine.common.tile.generator.TileEntityLargeWindGenerator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class MultiblockMachineCommonProxy implements IGuiProvider {


    private static void registerTileEntity(Class<? extends TileEntity> clazz, String name) {
        GameRegistry.registerTileEntity(clazz, new ResourceLocation(MekanismMultiblockMachine.MODID, name));
    }


    public void registerTileEntities() {
        registerTileEntity(TileEntityLargeWindGenerator.class, "large_wind_Generator");
    }

    public void registerTESRs() {
    }

    public void registerItemRenders() {
    }

    public void registerBlockRenders() {
    }

    public void preInit() {
    }

    public void loadConfiguration() {
        MekanismConfig.local().multiblock.load(Mekanism.configurationMultiblockMachine);
        if (Mekanism.configurationMultiblockMachine.hasChanged()) {
            Mekanism.configurationMultiblockMachine.save();
        }
    }

    @Override
    public Object getClientGui(int ID, EntityPlayer player, World world, BlockPos pos) {
        return null;
    }

    @Override
    public Container getServerGui(int ID, EntityPlayer player, World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        return switch (ID) {
            case 0 -> new ContainerLargeWindGenerator(player.inventory, (TileEntityLargeWindGenerator) tileEntity);
            default -> null;
        };
    }


}
