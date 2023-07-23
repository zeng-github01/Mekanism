package mekanism.client.gui.button;

import mekanism.api.EnumColor;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.util.MekanismUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

@SideOnly(Side.CLIENT)
public class GuiColorButton extends GuiButton {

    private final Supplier<EnumColor> colorSupplier;
    private final ResourceLocation Slot = MekanismUtils.getResource(MekanismUtils.ResourceType.SLOT, "Slot_Icon.png");

    public GuiColorButton(int id, int x, int y, Supplier<EnumColor> colorSupplier) {
        super(id, x, y, 16, 16, "");
        this.colorSupplier = colorSupplier;
    }

    @Override
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            //Ensure the color gets reset. The default GuiButtonImage doesn't so other GuiButton's can have the color leak out of them
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            mc.getTextureManager().bindTexture(Slot);
            drawTexturedModalRect(x - 1, y - 1, 0, 0, 18, 18);
            EnumColor color = colorSupplier.get();
            if (color != null) {
                drawRect(this.x, this.y, this.x + this.width, this.y + this.height, MekanismRenderer.getColorARGB(color, 1));
                MekanismRenderer.resetColor();
            }
        }
    }
}