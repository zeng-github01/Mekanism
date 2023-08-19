package mekanism.client.gui.element;

import mekanism.client.gui.IGuiWrapper;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPlayerSlot extends GuiElement {

    private final int xLocation;
    private final int yLocation;



    public GuiPlayerSlot(IGuiWrapper gui, ResourceLocation def, int x, int y) {
        super(MekanismUtils.getResource(ResourceType.SLOT, "Slot_Icon.png"), gui, def);
        xLocation = x;
        yLocation = y;

    }

    public GuiPlayerSlot(IGuiWrapper gui, ResourceLocation def) {
        super(MekanismUtils.getResource(ResourceType.SLOT, "Slot_Icon.png"), gui, def);
        xLocation = 7;
        yLocation = 83;
    }

    @Override
    public Rectangle4i getBounds(int guiWidth, int guiHeight) {
        return new Rectangle4i(guiWidth + xLocation, guiHeight + yLocation, 18, 18);
    }

    @Override
    public void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight) {
        mc.renderEngine.bindTexture(RESOURCE);
        for (int y =0; y<3;y++) {
            for (int x = 0; x < 9; x++) {
                guiObj.drawTexturedRect(guiWidth + xLocation + x * 18, guiHeight + yLocation +  y * 18, 0, 0, 18, 18);
            }
        }
            for (int x = 0; x < 9; x++) {
                guiObj.drawTexturedRect(guiWidth + xLocation + x * 18, guiHeight + yLocation + 58, 0, 0, 18, 18);
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
