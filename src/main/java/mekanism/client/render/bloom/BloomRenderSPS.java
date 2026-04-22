package mekanism.client.render.bloom;

import gregtech.client.utils.EffectRenderContext;
import mekanism.client.render.tileentity.RenderSPS;
import mekanism.common.tile.multiblock.TileEntitySPSCasing;
import mekanism.common.util.BloomEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

@SideOnly(Side.CLIENT)
public class BloomRenderSPS extends BloomEffect<TileEntitySPSCasing> {

    private final TileEntitySPSCasing tile;

    public BloomRenderSPS(TileEntitySPSCasing tile) {
        super(tile, 0, 0, 0, 0);
        this.tile = tile;
    }

    @Override
    public boolean shouldRenderBloomEffect(@NotNull EffectRenderContext context) {
        return RenderSPS.hasBoltsToRender(tile) && super.shouldRenderBloomEffect(context);
    }

    @Override
    public void renderBloomEffect(@NotNull BufferBuilder bufferBuilder, @NotNull EffectRenderContext effectRenderContext) {
        if (!RenderSPS.hasBoltsToRender(tile)) {
            return;
        }
        GlStateManager.pushMatrix();
        BlockPos pos = tile.getPos();
        GlStateManager.translate(pos.getX() - effectRenderContext.cameraX(), pos.getY() - effectRenderContext.cameraY(), pos.getZ() - effectRenderContext.cameraZ());
        float partialTick = Minecraft.getMinecraft().isGamePaused() ? 0 : Minecraft.getMinecraft().getRenderPartialTicks();
        RenderSPS.renderBloomBolts(tile, partialTick);
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.popMatrix();
    }

    @Override
    protected void RenderModelBloom() {
    }
}
