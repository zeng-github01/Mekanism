package mekanism.client.gui.element;

import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.tab.GuiSideHolder;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPlayerArmmorSlot extends GuiSideHolder {

    private final int xLocation;
    private final int yLocation;
    private final boolean left;

    public GuiPlayerArmmorSlot(IGuiWrapper gui, ResourceLocation def, int x, int y, boolean isleft) {
        super(gui, def, x, y, 26, 98, isleft);
        xLocation = x;
        yLocation = y;
        left = isleft;
    }


    @Override
    public void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight) {
        super.renderBackground(xAxis,yAxis,guiWidth,guiHeight);
        mc.renderEngine.bindTexture(MekanismUtils.getResource(ResourceType.SLOT, "Slot_Icon.png"));
        for (int y = 0; y < 5; y++) {
            guiObj.drawTexturedRect(guiWidth + xLocation + (left ? 5 : 3), guiHeight + yLocation + y * 18 + 4, 0, 0, 18, 18);
        }
        mc.renderEngine.bindTexture(defaultLocation);
    }

    @Override
    public void renderForeground(int xAxis, int yAxis) {
    }


    @Override
    public void preMouseClicked(int xAxis, int yAxis, int button) {
    }

    @Override
    public void mouseClicked(int xAxis, int yAxis, int button) {
    }


}
