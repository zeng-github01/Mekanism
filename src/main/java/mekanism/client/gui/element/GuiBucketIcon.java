package mekanism.client.gui.element;

import mekanism.client.gui.IGuiWrapper;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBucketIcon extends GuiElement {

    private final int xLocation;
    private final int yLocation;

    private final int textureX;
    private final int textureY;


    private final int width;
    private final int height;


    public GuiBucketIcon(IconType type, IGuiWrapper gui, ResourceLocation def, int x, int y) {
        super(MekanismUtils.getResource(ResourceType.GUI_ELEMENT, "GuiBucket.png"), gui, def);
        xLocation = x;
        yLocation = y;
        width = type.width;
        height = type.height;

        textureX = type.textureX;
        textureY = type.textureY;
    }

    @Override
    public Rectangle4i getBounds(int guiWidth, int guiHeight) {
        return new Rectangle4i(guiWidth + xLocation, guiHeight + yLocation, width, height);
    }

    @Override
    public void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight) {
        mc.renderEngine.bindTexture(RESOURCE);
        guiObj.drawTexturedRect(guiWidth + xLocation, guiHeight + yLocation, textureX, textureY, width, height);
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

    public enum IconType {
        BUCKET(12, 13, 0, 1),
        INDUCTIONMATRIX(26, 57, 0, 16),
        DYNAMICTANK(26, 57, 0, 75);

        public final int width;
        public final int height;

        public final int textureX;
        public final int textureY;

        IconType(int w, int h, int x, int y) {
            width = w;
            height = h;
            textureX = x;
            textureY = y;
        }
    }
}