package mekanism.client.gui.button;

import mekanism.api.EnumColor;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.SideData;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.util.MekanismUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

@SideOnly(Side.CLIENT)
public class GuiSideDataButton extends GuiButton {

    private final Supplier<SideData> sideDataSupplier;
    private final Supplier<EnumColor> colorSupplier;
    private final ResourceLocation Button = MekanismUtils.getResource(MekanismUtils.ResourceType.BUTTON, "Button.png");
    private final int slotPosMapIndex;

    public GuiSideDataButton(int id, int x, int y, int slotPosMapIndex, Supplier<SideData> sideDataSupplier, Supplier<EnumColor> colorSupplier) {
        super(id, x, y, 14, 14, "");
        this.slotPosMapIndex = slotPosMapIndex;
        this.sideDataSupplier = sideDataSupplier;
        this.colorSupplier = colorSupplier;
    }

    @Override
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int halfWidthLeft = width / 2;
            int halfWidthRight = width % 2 == 0 ? halfWidthLeft : halfWidthLeft + 1;
            int halfHeightTop = height / 2;
            int halfHeightBottom = height % 2 == 0 ? halfHeightTop : halfHeightTop + 1;
            mc.getTextureManager().bindTexture(Button);
            SideData data = sideDataSupplier.get();
            if (data == TileComponentConfig.EMPTY) {
                drawTexturedModalRect(x, y, 0, 0, halfWidthLeft, halfHeightTop);
                drawTexturedModalRect(x, y + halfHeightTop, 0, 20 - halfHeightBottom, halfWidthLeft, halfHeightBottom);
                drawTexturedModalRect(x + halfWidthLeft, y, 200 - halfWidthRight, 0, halfWidthRight, halfHeightTop);
                drawTexturedModalRect(x + halfWidthLeft, y + halfHeightTop, 200 - halfWidthRight, 20 - halfHeightBottom, halfWidthRight, halfHeightBottom);
            } else {
                EnumColor color = getColor();
                boolean doColor = color != null && color != EnumColor.GREY;
                if (doColor) {
                    MekanismRenderer.color(getColor());
                }
                int Focusorhover = this.hovered ? 40 : 20;
                drawTexturedModalRect(x, y, 0, Focusorhover, halfWidthLeft, halfHeightTop);
                drawTexturedModalRect(x, y + halfHeightTop, 0, Focusorhover + 20 - halfHeightBottom, halfWidthLeft, halfHeightBottom);
                drawTexturedModalRect(x + halfWidthLeft, y, 200 - halfWidthRight, Focusorhover, halfWidthRight, halfHeightTop);
                drawTexturedModalRect(x + halfWidthLeft, y + halfHeightTop, 200 - halfWidthRight, Focusorhover + 20 - halfHeightBottom, halfWidthRight, halfHeightBottom);
                if (doColor) {
                    MekanismRenderer.resetColor();
                }
            }
        }
    }

    public int getSlotPosMapIndex() {
        return this.slotPosMapIndex;
    }

    public SideData getSideData() {
        return this.sideDataSupplier.get();
    }

    public EnumColor getColor() {
        return this.colorSupplier.get();
    }
}