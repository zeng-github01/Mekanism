package mekanism.client.render.entity.baby;

import mekanism.client.model.baby.ModelBabyCreeper;
import mekanism.client.render.entity.layers.BabyCreeperChargeLayer;
import mekanism.common.entity.baby.EntityBabyCreeper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

public class RenderBabyCreeper extends RenderLiving<EntityBabyCreeper> {

    private static final ResourceLocation CREEPER_TEXTURES = new ResourceLocation("textures/entity/creeper/creeper.png");

    public RenderBabyCreeper(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelBabyCreeper(), 0.5F);
        addLayer(new BabyCreeperChargeLayer(this));
    }


    /**
     * Allows the render to do state modifications necessary before the model is rendered.
     */
    @Override
    protected void preRenderCallback(EntityBabyCreeper entitylivingbaseIn, float partialTickTime) {
        float f = entitylivingbaseIn.getCreeperFlashIntensity(partialTickTime);
        float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
        f = MathHelper.clamp(f, 0.0F, 1.0F);
        f = f * f;
        f = f * f;
        float f2 = (1.0F + f * 0.4F) * f1;
        float f3 = (1.0F + f * 0.1F) / f1;
        GlStateManager.scale(f2, f3, f2);
    }

    @Override
    protected int getColorMultiplier(EntityBabyCreeper entitylivingbaseIn, float lightBrightness, float partialTickTime) {
        float f = entitylivingbaseIn.getCreeperFlashIntensity(partialTickTime);
        if ((int) (f * 10.0F) % 2 == 0) {
            return 0;
        } else {
            int i = (int) (f * 0.2F * 255.0F);
            i = MathHelper.clamp(i, 0, 255);
            return i << 24 | 822083583;
        }
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityBabyCreeper entity) {
        return CREEPER_TEXTURES;
    }
}
