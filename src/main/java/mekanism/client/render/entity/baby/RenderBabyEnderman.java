package mekanism.client.render.entity.baby;

import mekanism.client.model.baby.ModelBabyEnderman;
import mekanism.client.render.entity.layers.BabyEndermanEyesLayer;
import mekanism.client.render.entity.layers.BabyEndermanHeldBlockLayer;
import mekanism.common.entity.baby.EntityBabyEnderman;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class RenderBabyEnderman extends RenderLiving<EntityBabyEnderman> {

    private static final ResourceLocation ENDERMAN_TEXTURES = new ResourceLocation("textures/entity/enderman/enderman.png");
    private final Random rnd = new Random();

    public RenderBabyEnderman(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelBabyEnderman(), 0.5F);
        addLayer(new BabyEndermanEyesLayer(this));
        addLayer(new BabyEndermanHeldBlockLayer(this));
    }

    public void doRender(EntityBabyEnderman entity, double x, double y, double z, float entityYaw, float partialTicks) {
        IBlockState iblockstate = entity.getHeldBlockState();
        ModelBabyEnderman modelenderman = this.getMainModel();
        modelenderman.isCarrying = iblockstate != null;
        modelenderman.isAttacking = entity.isScreaming();
        if (entity.isScreaming()) {
            x += this.rnd.nextGaussian() * 0.02D;
            z += this.rnd.nextGaussian() * 0.02D;
        }

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    public ModelBabyEnderman getMainModel() {
        return (ModelBabyEnderman) super.getMainModel();
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityBabyEnderman entity) {
        return ENDERMAN_TEXTURES;
    }
}
