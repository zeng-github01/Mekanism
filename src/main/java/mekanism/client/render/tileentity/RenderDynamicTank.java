package mekanism.client.render.tileentity;

import mekanism.client.render.FluidRenderer;
import mekanism.client.render.FluidRenderer.RenderData;
import mekanism.client.render.FluidRenderer.ValveRenderData;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.GlowInfo;
import mekanism.common.content.tank.SynchronizedTankData.ValveData;
import mekanism.common.tile.multiblock.TileEntityDynamicTank;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderDynamicTank extends TileEntitySpecialRenderer<TileEntityDynamicTank> {


    @Override
    public void render(TileEntityDynamicTank tileEntity, double x, double y, double z, float partialTick, int destroyStage, float alpha) {
        boolean glChanged = false;
        if (tileEntity.clientHasStructure && tileEntity.isRendering && tileEntity.structure != null && tileEntity.structure.fluidStored != null && tileEntity.structure.fluidStored.amount != 0) {
            RenderData data = new RenderData();
            data.location = tileEntity.structure.renderLocation;
            data.height = tileEntity.structure.volHeight - 2;
            data.length = tileEntity.structure.volLength;
            data.width = tileEntity.structure.volWidth;
            data.fluidType = tileEntity.structure.fluidStored;

            if (data.location != null && data.height >= 1) {
                GlStateManager.pushMatrix();
                glChanged = enableGL();
                bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                FluidRenderer.translateToOrigin(data.location);
                GlowInfo glowInfo = MekanismRenderer.enableGlow(data.fluidType);
                MekanismRenderer.color(data.fluidType, (float) data.fluidType.amount / (float) tileEntity.clientCapacity);
                if (data.fluidType.getFluid().isGaseous(data.fluidType)) {
                    FluidRenderer.getTankDisplay(data).render();
                } else {
                    FluidRenderer.getTankDisplay(data, tileEntity.prevScale).render();
                }

                MekanismRenderer.resetColor();
                MekanismRenderer.disableGlow(glowInfo);
                GlStateManager.popMatrix();

                for (ValveData valveData : tileEntity.valveViewing) {
                    GlStateManager.pushMatrix();
                    glChanged = enableGL();
                    bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                    FluidRenderer.translateToOrigin(valveData.location);
                    GlowInfo valveGlowInfo = MekanismRenderer.enableGlow(data.fluidType);
                    MekanismRenderer.color(data.fluidType);
                    FluidRenderer.getValveDisplay(ValveRenderData.get(data, valveData)).render();
                    MekanismRenderer.resetColor();
                    MekanismRenderer.disableGlow(valveGlowInfo);
                    GlStateManager.popMatrix();
                }

                if (glChanged) {
                    setLightmapDisabled(false);
                    GlStateManager.disableBlend();
                    GlStateManager.enableAlpha();
                    GlStateManager.enableLighting();
                    GlStateManager.disableCull();
                }
            }
        }
    }

    private boolean enableGL() {
        GlStateManager.enableCull();
        GlStateManager.disableLighting();
        GlStateManager.shadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        setLightmapDisabled(true);
        return true;
    }
}
