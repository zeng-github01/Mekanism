package mekanism.multiblockmachine.client;

import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.item.ItemLayerWrapper;
import mekanism.multiblockmachine.client.gui.GuiLargeWindGenerator;
import mekanism.multiblockmachine.client.render.RenderLargeWindGenerator;
import mekanism.multiblockmachine.client.render.item.RenderMultiblockGeneratorItem;
import mekanism.multiblockmachine.common.MekanismMultiblockMachine;
import mekanism.multiblockmachine.common.MultiblockMachineBlocks;
import mekanism.multiblockmachine.common.MultiblockMachineCommonProxy;
import mekanism.multiblockmachine.common.block.states.BlockStateMultiblockMachineGenerator.MultiblockMachineGeneratorBlockStateMapper;
import mekanism.multiblockmachine.common.block.states.BlockStateMultiblockMachineGenerator.MultiblockMachineGeneratorType;
import mekanism.multiblockmachine.common.tile.generator.TileEntityLargeWindGenerator;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MultiblockMachineClientProxy extends MultiblockMachineCommonProxy {

    private static final IStateMapper generatorMapper = new MultiblockMachineGeneratorBlockStateMapper();

    @Override
    public void registerTESRs() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLargeWindGenerator.class, new RenderLargeWindGenerator());
    }

    @Override
    public void registerItemRenders() {
        Item.getItemFromBlock(MultiblockMachineBlocks.MultiblockGenerator).setTileEntityItemStackRenderer(new RenderMultiblockGeneratorItem());
    }

    @Override
    public void registerBlockRenders() {
        ModelLoader.setCustomStateMapper(MultiblockMachineBlocks.MultiblockGenerator, generatorMapper);
        for (MultiblockMachineGeneratorType type : MultiblockMachineGeneratorType.values()) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(type.blockType.getBlock()), type.meta, new ModelResourceLocation(new ResourceLocation(MekanismMultiblockMachine.MODID, type.getName()), "inventory"));
        }
    }

    public void registerItemRender(Item item) {
        MekanismRenderer.registerItemRender(MekanismMultiblockMachine.MODID, item);
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent event) {
        IRegistry<ModelResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
        generatorModelBake(modelRegistry, MultiblockMachineGeneratorType.LARGE_WIND_GENERATOR);
    }

    private void generatorModelBake(IRegistry<ModelResourceLocation, IBakedModel> modelRegistry, MultiblockMachineGeneratorType type) {
        ModelResourceLocation modelResourceLocation = new ModelResourceLocation(new ResourceLocation(MekanismMultiblockMachine.MODID, type.getName()), "inventory");
        ItemLayerWrapper itemLayerWrapper = new ItemLayerWrapper(modelRegistry.getObject(modelResourceLocation));
        RenderMultiblockGeneratorItem.modelMap.put(type, itemLayerWrapper);
        modelRegistry.putObject(modelResourceLocation, itemLayerWrapper);
    }

    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public GuiScreen getClientGui(int ID, EntityPlayer player, World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        return switch (ID) {
            case 0 -> new GuiLargeWindGenerator(player.inventory, (TileEntityLargeWindGenerator) tileEntity);
            default -> null;
        };
    }

    @SubscribeEvent
    public void onStitch(TextureStitchEvent.Pre event) {
    }
}
