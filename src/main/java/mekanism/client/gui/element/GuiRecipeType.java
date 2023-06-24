package mekanism.client.gui.element;

import mekanism.client.gui.IGuiWrapper;
import mekanism.common.tile.TileEntityFactory;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiRecipeType extends GuiTileEntityElement<TileEntityFactory> {

    private final int xLocation;
    private final int yLocation;

    public GuiRecipeType(IGuiWrapper gui, TileEntityFactory tile, ResourceLocation def, int x, int y) {
        super(MekanismUtils.getResource(ResourceType.GUI_ELEMENT, "GuiRecipeType.png"), gui, def, tile);
        xLocation = x;
        yLocation = y;
    }

    @Override
    public Rectangle4i getBounds(int guiWidth, int guiHeight) {
        return new Rectangle4i(guiWidth + 176 + xLocation, guiHeight + 70 + yLocation, 26, 63);
    }

    @Override
    protected boolean inBounds(int xAxis, int yAxis) {
        return xAxis >= 180 + xLocation && xAxis <= 196 + xLocation && (yAxis >= 75 + yLocation && yAxis <= 91 + yLocation || yAxis >= 112 + yLocation && yAxis <= 128 + yLocation);
    }

    @Override
    public void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight) {
        mc.renderEngine.bindTexture(RESOURCE);
        guiObj.drawTexturedRect(guiWidth + 176 + xLocation, guiHeight + 70 + yLocation, 0, 0, 26, 63);
        int displayInt = tileEntity.getScaledRecipeProgress(15);
        guiObj.drawTexturedRect(guiWidth + 181 + xLocation, guiHeight + 94 + yLocation, 26, 0, 10, displayInt);
        mc.renderEngine.bindTexture(defaultLocation);
    }

    @Override
    public void renderForeground(int xAxis, int yAxis) {
    }

    @Override
    public void preMouseClicked(int xAxis, int yAxis, int button) {
        if (button == 0 && inBounds(xAxis, yAxis)) {
            offsetX(26);
        }
    }

    @Override
    public void mouseClicked(int xAxis, int yAxis, int button) {
        if (button == 0 && inBounds(xAxis, yAxis)) {
            offsetX(-26);
        }
    }
}