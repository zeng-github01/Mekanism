package mekanism.client.render.bloom;

import gregtech.client.utils.BloomEffectUtil;
import mekanism.client.model.mekasuitarmour.IMekaSuitBloomModel;
import mekanism.client.model.mekasuitarmour.ModelMekAsuitBodyArm;
import mekanism.common.item.armor.ItemMekaSuitArmor;
import mekanism.common.item.armor.ItemMekaSuitBodyArmor;
import mekanism.common.lib.Color;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped.ArmPose;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

public final class MekaSuitFirstPersonBloomHelper {

    private static final float ARM_SCALE = 0.0625F;

    private MekaSuitFirstPersonBloomHelper() {
    }

    public static void requestArmBloom(AbstractClientPlayer player, boolean rightHand) {
        if (player == null || !IMekaSuitBloomModel.shouldUseBloom()) {
            return;
        }
        ItemStack chestStack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (!(chestStack.getItem() instanceof ItemMekaSuitBodyArmor)) {
            return;
        }
        MatrixSnapshot snapshot = MatrixSnapshot.capture();
        BloomEffectUtil.requestCustomBloom(new BloomEffectUtil.IBloomRenderFast() {
            @Override
            public int customBloomStyle() {
                return mekanism.common.config.MekanismConfig.current().client.customBloomStyle.val();
            }

            @Override
            public void preDraw(BufferBuilder bufferBuilder) {
            }

            @Override
            public void postDraw(BufferBuilder bufferBuilder) {
            }
        }, bufferBuilder -> renderArmBloom(player, rightHand, snapshot));
    }

    private static void renderArmBloom(AbstractClientPlayer player, boolean rightHand, MatrixSnapshot snapshot) {
        ItemStack chestStack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (!(chestStack.getItem() instanceof ItemMekaSuitBodyArmor)) {
            return;
        }
        ModelMekAsuitBodyArm armor = ModelMekAsuitBodyArm.armorModel;
        armor.setVisible(true);
        armor.leftArmPose = ArmPose.EMPTY;
        armor.rightArmPose = ArmPose.EMPTY;
        armor.isSneak = false;
        armor.swingProgress = 0.0F;
        armor.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, ARM_SCALE, player);
        snapshot.apply(() -> {
            Color color = getBloomTint(chestStack);
            GlStateManager.disableBlend();
            GlStateManager.color(color.rf(), color.gf(), color.bf(), 1.0F);
            if (rightHand) {
                armor.rightArmRenderBloom(ARM_SCALE);
            } else {
                armor.leftArmRenderBloom(ARM_SCALE);
            }
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        });
    }

    private static Color getBloomTint(ItemStack chestStack) {
        Color color = ItemMekaSuitArmor.getColorModulation(chestStack);
        if (color == null) {
            return Color.WHITE;
        }
        double tintStrength = color.ad();
        double tintBase = 1.0D - tintStrength;
        return Color.rgbd(color.rd() * tintStrength + tintBase, color.gd() * tintStrength + tintBase, color.bd() * tintStrength + tintBase);
    }

    private static final class MatrixSnapshot {

        private final FloatBuffer modelView;
        private final FloatBuffer projection;

        private MatrixSnapshot(FloatBuffer modelView, FloatBuffer projection) {
            this.modelView = modelView;
            this.projection = projection;
        }

        private static MatrixSnapshot capture() {
            FloatBuffer modelView = BufferUtils.createFloatBuffer(16);
            FloatBuffer projection = BufferUtils.createFloatBuffer(16);
            GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelView);
            GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
            modelView.rewind();
            projection.rewind();
            return new MatrixSnapshot(modelView, projection);
        }

        private void apply(Runnable renderer) {
            int previousMatrixMode = GL11.glGetInteger(GL11.GL_MATRIX_MODE);
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPushMatrix();
            projection.rewind();
            GL11.glLoadMatrix(projection);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPushMatrix();
            modelView.rewind();
            GL11.glLoadMatrix(modelView);
            renderer.run();
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glPopMatrix();
            GL11.glMatrixMode(previousMatrixMode);
        }
    }
}
