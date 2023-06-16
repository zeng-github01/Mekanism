package mekanism.client.gui.element;

import mekanism.client.gui.IGuiWrapper;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiBlackScreen extends GuiElement {

    private final int xLocation;
    private final int yLocation;
    private final int textureX;
    private final int textureY;

    private final int width;
    private final int height;

    public GuiBlackScreen(BlackScreen type, IGuiWrapper gui, ResourceLocation def, int x, int y) {
        super(MekanismUtils.getResource(ResourceType.GUI_ELEMENT, "GuiBlackScreen.png"), gui, def);

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

    public enum BlackScreen {

        BIO_EVAPORATION(80,40,0,0),
        CRYSTALLIZER(121,42,81,0),
        DYNAMICTANK_INDUCTIONMATRIX_PUMP(80,41,0,41),
        SEISMICVIBRATOR(112,40,81,44),
        THERMOELECTRICBOILER(96,32,0,84),
        GASTANK(118,27,97,84),
        FUELWOOD(80,28,0,117),
        SIDECONFIG(74,12,81,117)
        ;


        public final int width;
        public final int height;

        public final int textureX;
        public final int textureY;

        BlackScreen(int w, int h, int x, int y) {
            width = w;
            height = h;

            textureX = x;
            textureY = y;
        }
    }

}