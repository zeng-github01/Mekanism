package mekanism.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelArmoredFreeRunners extends ModelBase {
    ModelRenderer SpringL;
    ModelRenderer SpringR;
    ModelRenderer BraceL;
    ModelRenderer BraceR;
    ModelRenderer SupportL;
    ModelRenderer SupportR;
    ModelRenderer PlateL;
    ModelRenderer PlateR;
    ModelRenderer TopPlateL;
    ModelRenderer TopPlateR;
    ModelRenderer ConnectionL;
    ModelRenderer ConnectionR;
    ModelRenderer ArmoredBraceL;
    ModelRenderer ArmoredBraceR;
    ModelRenderer BatteryL;
    ModelRenderer BatteryR;

    public ModelArmoredFreeRunners(){
        textureWidth = 64;
        textureHeight = 32;

        SpringL = new ModelRenderer(this, 8, 0);
        SpringL.addBox(1.5F, 18F, 0F, 1, 6, 1);
        SpringL.setRotationPoint(0F, 0F, 0F);
        SpringL.setTextureSize(64, 32);
        SpringL.mirror = true;
        setRotation(SpringL, 0.1047198F, 0F, 0F);
        SpringR = new ModelRenderer(this, 8, 0);
        SpringR.addBox(-2.5F, 18F, 0F, 1, 6, 1);
        SpringR.setRotationPoint(0F, 0F, 0F);
        SpringR.setTextureSize(64, 32);
        SpringR.mirror = true;
        setRotation(SpringR, 0.1047198F, 0F, 0F);
        SpringR.mirror = false;
        BraceL = new ModelRenderer(this, 12, 0);
        BraceL.addBox(0.2F, 18F, -0.8F, 4, 2, 3);
        BraceL.setRotationPoint(0F, 0F, 0F);
        BraceL.setTextureSize(64, 32);
        BraceL.mirror = true;
        setRotation(BraceL, 0F, 0F, 0F);
        BraceR = new ModelRenderer(this, 12, 0);
        BraceR.addBox(-4.2F, 18F, -0.8F, 4, 2, 3);
        BraceR.setRotationPoint(0F, 0F, 0F);
        BraceR.setTextureSize(64, 32);
        BraceR.mirror = true;
        setRotation(BraceR, 0F, 0F, 0F);
        SupportL = new ModelRenderer(this, 0, 0);
        SupportL.addBox(1F, 16.5F, -4.2F, 2, 4, 2);
        SupportL.setRotationPoint(0F, 0F, 0F);
        SupportL.setTextureSize(64, 32);
        SupportL.mirror = true;
        setRotation(SupportL, 0.296706F, 0F, 0F);
        SupportR = new ModelRenderer(this, 0, 0);
        SupportR.addBox(-3F, 16.5F, -4.2F, 2, 4, 2);
        SupportR.setRotationPoint(0F, 0F, 0F);
        SupportR.setTextureSize(64, 32);
        SupportR.mirror = true;
        setRotation(SupportR, 0.296706F, 0F, 0F);
        SupportR.mirror = false;

        PlateL = new ModelRenderer(this,0,11);
        PlateL.mirror = true;
        PlateL.addBox(0.5F,21,-3,3,2,1);
        PlateL.setTextureOffset(0,7);
        PlateL.addBox(0.5F, 17, -3, 3, 1, 1);

        PlateR = new ModelRenderer(this,0,11);
        PlateR.addBox(-3.5F, 21, -3, 3, 2, 1);
        PlateR.setTextureOffset(0,7);
        PlateR.addBox(-3.5F, 17, -3, 3, 1, 1);

        TopPlateL = new ModelRenderer(this,12,7);
        TopPlateL.mirror = true;
        TopPlateL.addBox(0, 0, -0.25F, 2, 2, 1);
        TopPlateL.setRotationPoint(-0.7854F,0,0);

        TopPlateR = new ModelRenderer(this,12,7);
        TopPlateR.addBox(-2, 0, -0.25F, 2, 2, 1);
        TopPlateR.setRotationPoint(-0.7854F,0,0);

        ConnectionL = new ModelRenderer(this);
        ConnectionL.mirror = true;
        ConnectionL.setTextureOffset(8,7);
        ConnectionL.addBox(2.5F, 18, -3, 1, 3, 1);
        ConnectionL.setTextureOffset(8,7);
        ConnectionL.addBox(0.5F, 18, -3, 1, 3, 1);

        ConnectionR = new ModelRenderer(this);
        ConnectionR.setTextureOffset(8,7);
        ConnectionR.addBox(-1.5F, 18, -3, 1, 3, 1);
        ConnectionR.setTextureOffset(8,7);
        ConnectionR.addBox(-3.5F, 18, -3, 1, 3, 1);

        ArmoredBraceL = new ModelRenderer(this);
        ArmoredBraceL.setTextureOffset(10,12);
        ArmoredBraceL.addBox(0.2F, 17, -2.3F, 4, 1, 1);
        ArmoredBraceL.setTextureOffset(8,10);
        ArmoredBraceL.addBox(0.2F, 21, -2.3F, 4, 1, 3);

        ArmoredBraceR = new ModelRenderer(this);
        ArmoredBraceR.mirror = true;
        ArmoredBraceR.setTextureOffset(10,12);
        ArmoredBraceR.addBox(-4.2F, 17, -2.3F, 4, 1, 1);
        ArmoredBraceR.setTextureOffset(8,10);
        ArmoredBraceR.addBox(-4.2F, 21, -2.3F, 4, 1, 3);

        BatteryL = new ModelRenderer(this);
        BatteryL.setTextureOffset(22,11);
        BatteryL.addBox(1.5F, 18, -3, 1, 2, 1);

        BatteryR = new ModelRenderer(this);
        BatteryR.setTextureOffset(22,11);
        BatteryR.addBox(-2.5F, 18, -3, 1, 2, 1);
    }

    public void render(float size) {
        SpringL.render(size);
        SpringR.render(size);
        BraceL.render(size);
        BraceR.render(size);
        SupportL.render(size);
        SupportR.render(size);
        PlateR.render(size);
        PlateL.render(size);
        //TODO Fix rendering bug for this part
//        TopPlateL.render(size);
//        TopPlateR.render(size);
        ConnectionL.render(size);
        ConnectionR.render(size);
        ArmoredBraceL.render(size);
        ArmoredBraceR.render(size);
        BatteryL.render(size);
        BatteryR.render(size);
    }

    public void renderLeft(float size) {
        SpringL.render(size);
        BraceL.render(size);
        SupportL.render(size);
        PlateL.render(size);
        //TODO Fix rendering bug for this part
//        TopPlateL.render(size);
        ConnectionL.render(size);
        ArmoredBraceL.render(size);
        BatteryL.render(size);
    }

    public void renderRight(float size) {
        SpringR.render(size);
        BraceR.render(size);
        SupportR.render(size);
        PlateR.render(size);
        //TODO Fix rendering bug for this part
//        TopPlateR.render(size);
        ConnectionR.render(size);
        ArmoredBraceR.render(size);
        BatteryR.render(size);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}
