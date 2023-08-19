package mekanism.client.gui.element.tab;

import mekanism.api.Coord4D;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiTileEntityElement;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.network.PacketSimpleGui.SimpleGuiMessage;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
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
        super(gui, def, tile, 176 + x, 6 + y, 26, 26, 179 + x, 10 + y, 18, 18);
        xLocation = x;
        yLocation = y;
    }


    public GuiUpgradeTab(IGuiWrapper gui, TileEntity tile, ResourceLocation def) {
        super(gui, def, tile, 176, 6, 26, 26, 179, 10, 18, 18);
        xLocation = 0;
        yLocation = 0;
    }


    @Override
    public void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight) {
        super.renderBackground(xAxis, yAxis, guiWidth, guiHeight);
        mc.renderEngine.bindTexture(MekanismUtils.getResource(MekanismUtils.ResourceType.BUTTON_TAB, "button_tab_icon.png"));
        guiObj.drawTexturedRect(guiWidth + 179 + xLocation, guiHeight + 10 + yLocation, 72, 18, 18, 18);
        mc.renderEngine.bindTexture(defaultLocation);
    }

    @Override
    public void renderForeground(int xAxis, int yAxis) {
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
