package mekanism.client.gui.element.tab;

import mekanism.api.Coord4D;
import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.tab.GuiMatrixTab.MatrixTab;
import mekanism.common.Mekanism;
import mekanism.common.network.PacketSimpleGui.SimpleGuiMessage;
import mekanism.common.tile.TileEntityInductionCasing;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMatrixTab extends GuiTabElementType<TileEntityInductionCasing, MatrixTab> {

    private MatrixTab tab;

    public GuiMatrixTab(IGuiWrapper gui, TileEntityInductionCasing tile, MatrixTab type, ResourceLocation def) {
        super(gui, tile, type, def);
        tab = type;
    }

    @Override
    public void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight) {
        super.renderBackground(xAxis, yAxis, guiWidth, guiHeight);
        mc.renderEngine.bindTexture(MekanismUtils.getResource(MekanismUtils.ResourceType.BUTTON_TAB, "button_tab_icon.png"));
        guiObj.drawTexturedRect(guiWidth - 21, guiHeight + tab.getYPos() + 4, tab.xlocation, tab.ylocation, 18, 18);
    }

    public enum MatrixTab implements TabType {
        MAIN(126, 0, 49, "gui.main"),
        STAT(198, 18, 50, "gui.matrixStats");

        private final String description;

        public final int xlocation;

        public final int ylocation;
        private final int guiId;

        MatrixTab(int x, int y, int id, String desc) {
            xlocation = x;
            ylocation = y;
            guiId = id;
            description = desc;
        }


        @Override
        public ResourceLocation getResource() {
            return MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "Null.png");
        }

        @Override
        public void openGui(TileEntity tile) {
            Mekanism.packetHandler.sendToServer(new SimpleGuiMessage(Coord4D.get(tile), 0, guiId));
        }

        @Override
        public String getDesc() {
            return LangUtils.localize(description);
        }

        @Override
        public int getYPos() {
            return 6;
        }


    }
}
