package mekanism.client.gui.element;

import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.tab.GuiSideHolder;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.util.MekanismUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiTileEntityElement<TILE extends TileEntity> extends GuiSideHolder {

    protected final TILE tileEntity;

    private final int ButtonX;

    private final int ButtonY;

    private final int ButtonsizeX;
    private final int ButtonsizeY;

    private boolean IsNull;

    public GuiTileEntityElement(IGuiWrapper gui, ResourceLocation def, TILE tile, int x, int y, int sizeX, int sizeY) {
        super(gui, def, x, y, sizeX, sizeY, x < 0);
        this.tileEntity = tile;
        ButtonX = 0;
        ButtonY = 0;
        ButtonsizeX = 0;
        ButtonsizeY = 0;
    }


    public GuiTileEntityElement(IGuiWrapper gui, ResourceLocation def, TILE tile, int x, int y, int sizeX, int sizeY, int ButtonX, int ButtonY, int ButtonsizeX, int ButtonsizeY) {
        super(gui, def, x, y, sizeX, sizeY, x < 0);
        this.tileEntity = tile;
        this.ButtonX = ButtonX;
        this.ButtonY = ButtonY;
        this.ButtonsizeX = ButtonsizeX;
        this.ButtonsizeY = ButtonsizeY;
    }

    @Override
    protected boolean inBounds(int xAxis, int yAxis) {
        return xAxis >= ButtonX && xAxis <= ButtonX + ButtonsizeX && yAxis >= ButtonY && yAxis <= ButtonY + ButtonsizeY;
    }

    protected int getHoverState(boolean hoveredOrFocused) {
        if (IsNull) {
            return 0;
        } else if (hoveredOrFocused) {
            return 2;
        }
        return 1;
    }

    @Override
    public void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight) {
        super.renderBackground(xAxis, yAxis, guiWidth, guiHeight);
        int i = getHoverState(inBounds(xAxis, yAxis));
        int position = i * 20;
        if (ButtonsizeX > 0 && ButtonsizeY > 0) {
            MekanismRenderer.resetColor();
            int halfWidthLeft = ButtonsizeX / 2;
            int halfWidthRight = ButtonsizeX % 2 == 0 ? halfWidthLeft : halfWidthLeft + 1;
            int halfHeightTop = ButtonsizeY / 2;
            int halfHeightBottom = ButtonsizeY % 2 == 0 ? halfHeightTop : halfHeightTop + 1;
            mc.getTextureManager().bindTexture(MekanismUtils.getResource(MekanismUtils.ResourceType.BUTTON, "Button.png"));
            guiObj.drawTexturedRect(guiWidth + ButtonX, guiHeight + ButtonY, 0, position, halfWidthLeft, halfHeightTop);
            guiObj.drawTexturedRect(guiWidth + ButtonX, guiHeight + ButtonY + halfHeightTop, 0, position + 20 - halfHeightBottom, halfWidthLeft, halfHeightBottom);
            guiObj.drawTexturedRect(guiWidth + ButtonX + halfWidthLeft, guiHeight + ButtonY, 200 - halfWidthRight, position, halfWidthRight, halfHeightTop);
            guiObj.drawTexturedRect(guiWidth + ButtonX + halfWidthLeft, guiHeight + ButtonY + halfHeightTop, 200 - halfWidthRight, position + 20 - halfHeightBottom, halfWidthRight, halfHeightBottom);
        }
    }

    public void setNull(boolean isNull) {
        IsNull = isNull;
    }
}
