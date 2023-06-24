package mekanism.client.gui.element;

import mekanism.client.gui.IGuiWrapper;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class GuiWarningInfo extends GuiElement {

    private final boolean HeatInfoenabled;

    public GuiWarningInfo(IGuiWrapper gui, ResourceLocation def, boolean enabled) {
        super(MekanismUtils.getResource(ResourceType.GUI_ELEMENT, "GuiWarningInfo.png"), gui, def);
        HeatInfoenabled = enabled;
    }

    @Override
    public Rectangle4i getBounds(int guiWidth, int guiHeight) {
        int height = HeatInfoenabled ? 26 : 0;
        return new Rectangle4i(guiWidth - 26, guiHeight + 112 - height, 26, 26);
    }

    @Override
    protected boolean inBounds(int xAxis, int yAxis) {
        int height = HeatInfoenabled ? 26 : 0;
        return xAxis >= -21 && xAxis <= -3 && yAxis >= 116 - height && yAxis <= 134 - height;
    }

    @Override
    public void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight) {
        mc.renderEngine.bindTexture(RESOURCE);
        int height = HeatInfoenabled ? 26 : 0;
        guiObj.drawTexturedRect(guiWidth - 26, guiHeight + 112 - height, 0, 0, 0, 0);
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