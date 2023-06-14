package mekanism.client.gui.element.tab;

import mekanism.api.Coord4D;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiTileEntityElement;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.network.PacketSimpleGui.SimpleGuiMessage;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiUpgradeTab extends GuiTileEntityElement<TileEntity> {

    private final int xLocation;
    private final int yLocation;

    public GuiUpgradeTab(IGuiWrapper gui, TileEntity tile, ResourceLocation def, int x, int y) {
        super(MekanismUtils.getResource(ResourceType.GUI_ELEMENT, "GuiUpgradeTab.png"), gui, def, tile);
        xLocation = x;
        yLocation = y;
    }

    @Override
    public Rectangle4i getBounds(int guiWidth, int guiHeight) {
        return new Rectangle4i(guiWidth + 176 + xLocation, guiHeight + 6 + yLocation, 26, 26);
    }

    @Override
    protected boolean inBounds(int xAxis, int yAxis) {
        return xAxis >= 179 + xLocation && xAxis <= 197 + xLocation && yAxis >= 10 + yLocation && yAxis <= 28 + yLocation;
    }

    @Override
    public void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight) {
        mc.renderEngine.bindTexture(RESOURCE);
        guiObj.drawTexturedRect(guiWidth + 176 + xLocation, guiHeight + 6 + yLocation, 0, 0, 26, 26);
        guiObj.drawTexturedRect(guiWidth + 179 + xLocation, guiHeight + 10 + yLocation, 26, inBounds(xAxis, yAxis) ? 0 : 18, 18, 18);
        mc.renderEngine.bindTexture(defaultLocation);
    }

    @Override
    public void renderForeground(int xAxis, int yAxis) {
        mc.renderEngine.bindTexture(RESOURCE);
        if (inBounds(xAxis, yAxis)) {
            displayTooltip(LangUtils.localize("gui.upgrades"), xAxis, yAxis);
        }
        mc.renderEngine.bindTexture(defaultLocation);
    }

    @Override
    public void preMouseClicked(int xAxis, int yAxis, int button) {
    }

    @Override
    public void mouseClicked(int xAxis, int yAxis, int button) {
        if (button == 0 && inBounds(xAxis, yAxis)) {
            Mekanism.packetHandler.sendToServer(new SimpleGuiMessage(Coord4D.get(tileEntity), 0, 43));
            SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
        }
    }
}