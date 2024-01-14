package mekanism.client.gui.element.tab;

import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiTileEntityElement;
import mekanism.client.sound.SoundHandler;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiTabElement<TILE extends TileEntity> extends GuiTileEntityElement<TILE> {

    protected final int yPos;

    public GuiTabElement(IGuiWrapper gui, ResourceLocation def, TILE tile, int y) {
        super(gui, def, tile, -26, y, 26, 26, -21, y + 4, 18, 18);
        yPos = y;
    }

    public abstract void displayForegroundTooltip(int xAxis, int yAxis);

    public abstract void buttonClicked();


    @Override
    public void mouseClicked(int xAxis, int yAxis, int button) {
        if (button == 0 && inBounds(xAxis, yAxis)) {
            buttonClicked();
            SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
        }
    }

    @Override
    public void preMouseClicked(int xAxis, int yAxis, int button) {
    }

    @Override
    public void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight) {
        super.renderBackground(xAxis, yAxis, guiWidth, guiHeight);
        mc.renderEngine.bindTexture(defaultLocation);
    }

    @Override
    public void renderForeground(int xAxis, int yAxis) {
        if (inBounds(xAxis, yAxis)) {
            displayForegroundTooltip(xAxis, yAxis);
        }
        mc.renderEngine.bindTexture(defaultLocation);
    }
}
