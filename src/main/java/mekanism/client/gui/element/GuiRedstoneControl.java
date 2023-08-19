package mekanism.client.gui.element;

import mekanism.api.Coord4D;
import mekanism.api.EnumColor;
import mekanism.api.util.time.Timeticks;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.base.IRedstoneControl;
import mekanism.common.base.IRedstoneControl.RedstoneControl;
import mekanism.common.network.PacketRedstoneControl.RedstoneControlMessage;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class GuiRedstoneControl extends GuiTileEntityElement<TileEntity> {

    private final int xLocation;
    private final int yLocation;
    protected Timeticks time = new Timeticks(20, 20, false);

    public GuiRedstoneControl(IGuiWrapper gui, TileEntity tile, ResourceLocation def, int x, int y) {
        super(gui, def, tile,176 + x,138 + y,26,26);
        xLocation = x;
        yLocation = y;
    }

    public GuiRedstoneControl(IGuiWrapper gui, TileEntity tile, ResourceLocation def) {
        super(gui, def, tile,176,138,26,26);
        xLocation = 0;
        yLocation = 0;
    }


    @Override
    protected boolean inBounds(int xAxis, int yAxis) {
        return xAxis >= 179 + xLocation && xAxis <= 197 + xLocation && yAxis >= 142 + yLocation && yAxis <= 160 + yLocation;
    }

    @Override
    public void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight) {
        MekanismRenderer.color(EnumColor.RED);
        super.renderBackground(xAxis,yAxis,guiWidth,guiHeight);
        MekanismRenderer.resetColor();
        mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.GUI_ELEMENT, "GuiRedstoneControl.png"));
        guiObj.drawTexturedRect(guiWidth + 176 + xLocation, guiHeight + 138 + yLocation, 0, 0, 26, 26);
        IRedstoneControl control = (IRedstoneControl) tileEntity;
        int renderX = 26 + (18 * control.getControlType().ordinal());
        if (control.getControlType() != RedstoneControl.PULSE) {
            guiObj.drawTexturedRect(guiWidth + 179 + xLocation, guiHeight + 142 + yLocation, renderX, 0, 18, 18);
        } else {
            int DynamicGUI = 0;
            double tick = time.getValue() / 20F;
            if (tick >= 0.1F && tick < 0.2F || tick >= 0.8F && tick < 0.9F) {
                DynamicGUI += 18;
            } else if (tick >= 0.2F && tick < 0.3F || tick >= 0.7F && tick < 0.8F) {
                DynamicGUI += 36;
            } else if (tick >= 0.3F && tick < 0.4F || tick >= 0.6F && tick < 0.7F) {
                DynamicGUI += 54;
            } else if (tick >= 0.4F && tick < 0.6F) {
                DynamicGUI += 72;
            }
            guiObj.drawTexturedRect(guiWidth + 179 + xLocation, guiHeight + 142 + yLocation, renderX, DynamicGUI, 18, 18);
        }
        mc.renderEngine.bindTexture(defaultLocation);
    }

    @Override
    public void renderForeground(int xAxis, int yAxis) {
        mc.renderEngine.bindTexture(RESOURCE);
        IRedstoneControl control = (IRedstoneControl) tileEntity;
        if (inBounds(xAxis, yAxis)) {
            displayTooltip(control.getControlType().getDisplay(), xAxis, yAxis);
        }
        mc.renderEngine.bindTexture(defaultLocation);
    }

    @Override
    public void preMouseClicked(int xAxis, int yAxis, int button) {
    }

    @Override
    public void mouseClicked(int xAxis, int yAxis, int button) {
        IRedstoneControl control = (IRedstoneControl) tileEntity;

        if (button == 0 && inBounds(xAxis, yAxis)) {
            RedstoneControl current = control.getControlType();
            int ordinalToSet = current.ordinal() < (RedstoneControl.values().length - 1) ? current.ordinal() + 1 : 0;
            if (ordinalToSet == RedstoneControl.PULSE.ordinal() && !control.canPulse()) {
                ordinalToSet = 0;
            }

            SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
            Mekanism.packetHandler.sendToServer(new RedstoneControlMessage(Coord4D.get(tileEntity), RedstoneControl.values()[ordinalToSet]));
        }
    }
}
