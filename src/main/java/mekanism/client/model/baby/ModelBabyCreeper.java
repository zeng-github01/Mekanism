package mekanism.client.model.baby;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelBabyCreeper extends ModelBase {

    public ModelRenderer head;
    public ModelRenderer body;
    public ModelRenderer leg1;
    public ModelRenderer leg2;
    public ModelRenderer leg3;
    public ModelRenderer leg4;

    public ModelBabyCreeper() {
        this(0.0F);
    }

    public ModelBabyCreeper(float size) {
        textureWidth = 64 / 2;
        textureHeight = 32 / 2;
        head = new ModelRenderer(this, 0, 0);
        head.addBox(-2.0F, -4.0F, -2.0F, 4, 4, 4, size);
        head.setRotationPoint(0.0F, 15.0F, 0.0F);

        body = new ModelRenderer(this, 16 / 2, 16 / 2);
        body.addBox(-2.0F, 0.0F, -1.0F, 4, 6, 2, size);
        body.setRotationPoint(0.0F, 15.0F, 0.0F);

        leg1 = new ModelRenderer(this, 0, 16 / 2);
        leg1.setRotationPoint(-1.0F, 21.0F, 2.0F);
        leg1.addBox(-1.0F, 0.0F, -1.0F, 2, 3, 2, size);

        leg2 = new ModelRenderer(this, 0, 16 / 2);
        leg2.setRotationPoint(1.0F, 21.0F, 2.0F);
        leg2.addBox(-1.0F, 0.0F, -1.0F, 2, 3, 2, size);

        leg3 = new ModelRenderer(this, 0, 16 / 2);
        leg3.setRotationPoint(-1.0F, 21.0F, -2.0F);
        leg3.addBox(-1.0F, 0.0F, -1.0F, 2, 3, 2, size);

        leg4 = new ModelRenderer(this, 0, 16 / 2);
        leg4.setRotationPoint(1.0F, 21.0F, -2.0F);
        leg4.addBox(-1.0F, 0.0F, -1.0F, 2, 3, 2, size);
    }

    /**
     * Sets the models various rotation angles then renders the model.
     */

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        this.head.render(scale);
        this.body.render(scale);
        this.leg1.render(scale);
        this.leg2.render(scale);
        this.leg3.render(scale);
        this.leg4.render(scale);
    }

    /**
     * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
     * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
     * "far" arms and legs can swing at most.
     */

    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        this.head.rotateAngleY = netHeadYaw * ((float) Math.PI / 180F);
        this.head.rotateAngleX = headPitch * ((float) Math.PI / 180F);
        this.leg1.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leg2.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.leg3.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.leg4.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
    }
}
