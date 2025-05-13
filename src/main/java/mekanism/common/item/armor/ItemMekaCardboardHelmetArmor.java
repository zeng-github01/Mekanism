package mekanism.common.item.armor;

import mekanism.client.render.MekanismRenderer;
import mekanism.common.CardboardArmorHandler;
import mekanism.common.util.MekanismUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class ItemMekaCardboardHelmetArmor extends ItemMekaCardboardArmor {


    protected static final ResourceLocation PUMPKIN_BLUR_TEX_PATH = MekanismUtils.getResource(MekanismUtils.ResourceType.ARMOR, "package_blur.png");

    public ItemMekaCardboardHelmetArmor() {
        super(0, EntityEquipmentSlot.HEAD);
    }


    @SideOnly(Side.CLIENT)
    @Override
    public void renderHelmetOverlay(ItemStack stack, EntityPlayer player, ScaledResolution resolution, float partialTicks) {
        if (!CardboardArmorHandler.testForStealth(player)) {
            return;
        }
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableAlpha();
        MekanismRenderer.bindTexture(PUMPKIN_BLUR_TEX_PATH);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(0.0D, resolution.getScaledHeight(), -90.0D).tex(0.0D, 1.0D).endVertex();
        bufferbuilder.pos(resolution.getScaledWidth(), resolution.getScaledHeight(), -90.0D);
        bufferbuilder.tex(1.0D, 1.0D);
        bufferbuilder.endVertex();
        bufferbuilder.pos(resolution.getScaledWidth(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }


}
