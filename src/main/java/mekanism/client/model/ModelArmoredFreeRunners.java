package mekanism.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
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

    public ModelArmoredFreeRunners() {
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

        PlateL = new ModelRenderer(this);
        PlateL.setRotationPoint(0F, 0F, 0F);
        PlateL.cubeList.add(new ModelBox(PlateL, 0, 11, 0.5F, 21F, -3F, 3, 2, 1, 0.0F, true));
        PlateL.cubeList.add(new ModelBox(PlateL, 0, 7, 0.5F, 17F, -3F, 3, 2, 1, 0.0F, true));

        PlateR = new ModelRenderer(this);
        PlateR.setRotationPoint(0F, 0F, 0F);
        PlateR.cubeList.add(new ModelBox(PlateR, 0, 11, -3.5F, 21F, -3F, 3, 2, 1, 0.0F, false));
        PlateR.cubeList.add(new ModelBox(PlateR, 0, 7, -3.5F, 17F, -3F, 3, 2, 1, 0.0F, false));

        TopPlateL = new ModelRenderer(this);
        TopPlateL.setRotationPoint(1.0F, 16.0F, -2.0F);
        setRotation(TopPlateL, -0.7854F, 0F, 0F);
        TopPlateL.cubeList.add(new ModelBox(TopPlateL, 12, 7, 0.0F, 0.0F, -0.25F, 2, 2, 1, 0.0F, true));

        TopPlateR = new ModelRenderer(this);
        TopPlateR.setRotationPoint(-1F, 16F, -2F);
        setRotation(TopPlateR, -0.7854F, 0F, 0F);
        TopPlateR.cubeList.add(new ModelBox(TopPlateR, 12, 7, -2F, 0F, -0.25F, 2, 2, 1, 0.0F, false));

        ConnectionL = new ModelRenderer(this);
        ConnectionL.setRotationPoint(0F, 0F, 0F);
        ConnectionL.cubeList.add(new ModelBox(ConnectionL, 8, 7, 2.5F, 18F, -3F, 1, 3, 1, 0.0F, true));
        ConnectionL.cubeList.add(new ModelBox(ConnectionL, 8, 7, 0.5F, 18f, -3f, 1, 3, 1, 0.0F, true));

        ConnectionR = new ModelRenderer(this);
        ConnectionR.setRotationPoint(0F, 0F, 0F);
        ConnectionR.cubeList.add(new ModelBox(ConnectionR, 8, 7, -1.5F, 18F, -3F, 1, 3, 1, 0.0F, false));
        ConnectionR.cubeList.add(new ModelBox(ConnectionR, 8, 7, -3.5F, 18F, -3F, 1, 3, 1, 0.0F, false));

        ArmoredBraceL = new ModelRenderer(this);
        ArmoredBraceL.setRotationPoint(0F, 0F, 0F);
        ArmoredBraceL.cubeList.add(new ModelBox(ArmoredBraceL, 10, 12, 0.2F, 17F, -2.3F, 4, 1, 1, 0.0F, false));
        ArmoredBraceL.cubeList.add(new ModelBox(ArmoredBraceL, 8, 10, 0.2F, 21F, -2.3F, 4, 1, 3, 0.0F, false));

        ArmoredBraceR = new ModelRenderer(this);
        ArmoredBraceR.setRotationPoint(0F, 0F, 0F);
        ArmoredBraceR.cubeList.add(new ModelBox(ArmoredBraceR, 10, 12, -4.2F, 17F, -2.3F, 4, 1, 1, 0.0F, false));
        ArmoredBraceR.cubeList.add(new ModelBox(ArmoredBraceR, 8, 10, -4.2F, 21F, -2.3F, 4, 1, 3, 0.0F, false));

        BatteryL = new ModelRenderer(this);
        BatteryL.setRotationPoint(0F, 0F, 0F);
        BatteryL.cubeList.add(new ModelBox(BatteryL, 22, 11, 1.5F, 18F, -3F, 1, 2, 1, 0.0F, false));

        BatteryR = new ModelRenderer(this);
        BatteryR.setRotationPoint(0F, 0F, 0F);
        BatteryR.cubeList.add(new ModelBox(BatteryR, 22, 11, -2.5F, 18F, -3F, 1, 2, 1, 0.0F, false));
    }

    public void render(float size) {
        SpringL.render(size);
        SpringR.render(size);
        BraceL.render(size);
        BraceR.render(size);
        SupportL.render(size);
        SupportR.render(size);
        PlateL.render(size);
        PlateR.render(size);
        TopPlateL.render(size);
        TopPlateR.render(size);
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
        TopPlateL.render(size);
        ConnectionL.render(size);
        ArmoredBraceL.render(size);
        BatteryL.render(size);
    }

    public void renderRight(float size) {
        SpringR.render(size);
        BraceR.render(size);
        SupportR.render(size);
        PlateR.render(size);
        TopPlateR.render(size);
        ConnectionR.render(size);
        ArmoredBraceR.render(size);
        BatteryR.render(size);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    private void setOffset(ModelRenderer model, float x, float y, float z) {
        model.offsetX = x;
        model.offsetY = y;
        model.offsetZ = z;
    }
}