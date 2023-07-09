package mekanism.client.gui.element;

import mekanism.client.gui.IGuiWrapper;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPlayerArmmorSlot extends GuiElement {

    private final int xLocation;
    private final int yLocation;

    private final boolean right;


    public GuiPlayerArmmorSlot(IGuiWrapper gui, ResourceLocation def, int x, int y,boolean isright) {
        super(MekanismUtils.getResource(ResourceType.GUI_ELEMENT, "GuiPlayerArmor.png"), gui, def);
        xLocation = x;
        yLocation = y;
        right = isright;
    }


    @Override
    public Rectangle4i getBounds(int guiWidth, int guiHeight) {
        return new Rectangle4i(guiWidth + xLocation, guiHeight + yLocation, 26, 98);
    }

    @Override
    public void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight) {
        mc.renderEngine.bindTexture(RESOURCE);
        guiObj.drawTexturedRect(guiWidth + xLocation, guiHeight + yLocation, 0, right ? 98 : 0, 26, 98);
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