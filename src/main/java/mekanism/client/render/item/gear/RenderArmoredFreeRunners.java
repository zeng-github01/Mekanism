package mekanism.client.render.item.gear;

import mekanism.client.model.ModelArmoredFreeRunners;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.item.ItemLayerWrapper;
import mekanism.client.render.item.MekanismItemStackRenderer;
import mekanism.common.util.MekanismUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RenderArmoredFreeRunners extends MekanismItemStackRenderer {

    public static ItemLayerWrapper model;
    private static ModelArmoredFreeRunners armoredFreeRunners = new ModelArmoredFreeRunners();
    @Override
    protected void renderBlockSpecific(@NotNull ItemStack stack, ItemCameraTransforms.TransformType transformType) {

    }

    @Override
    protected void renderItemSpecific(@NotNull ItemStack stack, ItemCameraTransforms.TransformType transformType) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(180, 0, 0, 1);
        GlStateManager.rotate(90, 0, -1, 0);
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        GlStateManager.translate(0.2F, -1.43F, 0.12F);
        MekanismRenderer.bindTexture(MekanismUtils.getResource(MekanismUtils.ResourceType.RENDER, "FreeRunners.png"));
        armoredFreeRunners.render(0.0625F);
        GlStateManager.popMatrix();
    }

    @NotNull @Override
    protected ItemCameraTransforms.TransformType getTransform(@NotNull ItemStack stack) {
        return model.getTransform();
    }
}
