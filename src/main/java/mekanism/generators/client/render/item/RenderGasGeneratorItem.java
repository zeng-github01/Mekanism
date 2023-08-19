package mekanism.generators.client.render.item;

import javax.annotation.Nonnull;
import mekanism.client.render.MekanismRenderer;
import mekanism.generators.client.model.ModelGasGenerator;
import mekanism.generators.common.util.MekanismGeneratorUtils;
import mekanism.generators.common.util.MekanismGeneratorUtils.ResourceType;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderGasGeneratorItem {

    private static ModelGasGenerator gasGenerator = new ModelGasGenerator();

    public static void renderStack(@Nonnull ItemStack stack, TransformType transformType) {
        GlStateManager.rotate(180, 0, 1, 1);
        GlStateManager.rotate(90, -1, 0, 0);
        GlStateManager.translate(0, -1.0F, 0);
        GlStateManager.rotate(180, 0, 1, 0);
        MekanismRenderer.bindTexture(MekanismGeneratorUtils.getResource(ResourceType.RENDER, "GasGenerator.png"));
        gasGenerator.render(0.0625F);
    }
}
