package mekanism.client.gui.element;

import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.tab.GuiSideHolder;
import mekanism.common.util.MekanismUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBucketIO extends GuiSideHolder {

    public GuiBucketIO(IGuiWrapper gui, ResourceLocation def) {
        super(gui, def, 176, 66, 26, 57);
    }


    @Override
    public void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight) {
        super.renderBackground(xAxis, yAxis, guiWidth, guiHeight);
        mc.renderEngine.bindTexture(MekanismUtils.getResource(MekanismUtils.ResourceType.SLOT, "Slot_Icon.png"));
        guiObj.drawTexturedRect(guiWidth + 179, guiHeight + 70, 36, 0, 18, 18);
        guiObj.drawTexturedRect(guiWidth + 179, guiHeight + 101, 72, 0, 18, 18);
        mc.renderEngine.bindTexture(MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "Other_Icon.png"));
        guiObj.drawTexturedRect(guiWidth + 184, guiHeight + 90, 13, 0, 8, 9);
        mc.renderEngine.bindTexture(defaultLocation);
    }

    @Override
    public void renderForeground(int xAxis, int yAxis) {
        mc.renderEngine.bindTexture(defaultLocation);
    }

    @Override
    protected boolean inBounds(int xAxis, int yAxis) {
        return xAxis >= 180 && xAxis <= 196 && yAxis >= 71 && yAxis <= 87 || xAxis >= 180 && xAxis <= 196 && yAxis >= 102 && yAxis <= 118;
    }

    @Override
    public void preMouseClicked(int xAxis, int yAxis, int button) {
        if (inBounds(xAxis, yAxis)) {
            offsetX(26);
        }
    }

    @Override
    public void mouseClicked(int xAxis, int yAxis, int button) {
        if (inBounds(xAxis, yAxis)) {
            offsetX(-26);
        }
    }
}
