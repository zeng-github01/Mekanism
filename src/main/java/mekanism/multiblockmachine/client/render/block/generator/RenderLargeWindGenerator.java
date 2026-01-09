package mekanism.multiblockmachine.client.render.block.generator;

import mekanism.client.Utils.RenderFastTileEntityTime;
import mekanism.client.Utils.RenderTileEntityTime;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.config.MekanismConfig;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.multiblockmachine.client.model.generator.ModelLargeWindGenerator;
import mekanism.multiblockmachine.common.MekanismMultiblockMachine;
import mekanism.multiblockmachine.common.tile.generator.TileEntityLargeWindGenerator;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderLargeWindGenerator extends RenderFastTileEntityTime<TileEntityLargeWindGenerator> {

    private final ModelLargeWindGenerator model = new ModelLargeWindGenerator();
    // Cache the main texture to avoid recreating/binding ResourceLocation repeatedly
    private static final ResourceLocation MAIN_TEXTURE = MekanismUtils.getResource(MekanismMultiblockMachine.MODID, ResourceType.RENDER, "WindGenerator/LargeWindGenerator.png");

    @Override
    public void renderTileEntityFast(TileEntityLargeWindGenerator tileEntity, double x, double y, double z, float partialTick, int destroyStage, float alpha, BufferBuilder buffer) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        bindTexture(MAIN_TEXTURE);
        MekanismRenderer.rotate(tileEntity.facing, 0, 180, 90, 270);
        GlStateManager.rotate(180, 0, 0, 1);
        if (MekanismConfig.current().client.largeWindGeneratorRending.val()) {
            model.renderBlock(getTime(), 0.0625F, angle(tileEntity, partialTick), tileEntity.getActive(), rendererDispatcher.renderEngine, true);
        }
        GlStateManager.popMatrix();
    }

    public double angle(TileEntityLargeWindGenerator tileEntity, float partialTick) {
        double angle = tileEntity.getAngle();
        if (tileEntity.getActive()) {
            angle = (tileEntity.getAngle() + ((tileEntity.getPos().getY() + 46F) / TileEntityLargeWindGenerator.SPEED_SCALED) * partialTick) % 360;
        }
        return angle;
    }

    public ModelLargeWindGenerator getModel() {
        return model;
    }

    @Override
    public boolean isGlobalRenderer(TileEntityLargeWindGenerator te) {
        return MekanismConfig.current().client.largeWindGeneratorisGlobalRenderer.val();
    }

}
