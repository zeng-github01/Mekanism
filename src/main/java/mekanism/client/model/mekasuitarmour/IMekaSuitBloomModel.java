package mekanism.client.model.mekasuitarmour;

import mekanism.client.render.MekanismRenderer;
import mekanism.client.render.MekanismRenderer.GlowInfo;
import mekanism.common.Mekanism;
import mekanism.common.config.MekanismConfig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public interface IMekaSuitBloomModel {

    static boolean shouldUseBloom() {
        return Mekanism.hooks.Bloom && MekanismConfig.current().client.enableBloom.val();
    }

    static boolean shouldRenderDirectGlow() {
        return !shouldUseBloom();
    }

    static GlowInfo prepareGlowRender() {
        if (shouldUseBloom()) {
            GlStateManager.disableLighting();
            return MekanismRenderer.NO_GLOW;
        }
        return MekanismRenderer.enableGlow();
    }

    static void finishGlowRender(GlowInfo glowInfo) {
        if (shouldUseBloom()) {
            GlStateManager.enableLighting();
        } else {
            MekanismRenderer.disableGlow(glowInfo);
        }
    }

    void renderBloom(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale);
}
