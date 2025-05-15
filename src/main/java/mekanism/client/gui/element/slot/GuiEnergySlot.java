package mekanism.client.gui.element.slot;

import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiSlot;
import mekanism.common.base.IMachineSlotTip;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEnergySlot extends GuiSlot {

    public GuiEnergySlot(IGuiWrapper gui, ResourceLocation def, int x, int y) {
        super(SlotType.POWER, gui, def, x, y);
        with(SlotOverlay.POWER);
    }

    public GuiEnergySlot(IGuiWrapper gui, ResourceLocation def, int x, int y, TileEntity tile) {
        super(SlotType.POWER, gui, def, x, y, new ISlotInfoHandler() {
            @Override
            public boolean getSlotCanTip() {
                return tile instanceof IMachineSlotTip tip && tip.getEnergySlot();
            }
        });
        with(SlotOverlay.POWER);
    }

    public GuiEnergySlot(IGuiWrapper gui, ResourceLocation def, int x, int y, ISlotInfoHandler handler) {
        super(SlotType.POWER, gui, def, x, y, handler);
        with(SlotOverlay.POWER);
    }
}
