package mekanism.client.gui.element.slot;

import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiSlot;
import mekanism.common.base.IMachineSlotTip;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiOutputSlot extends GuiSlot {


    public GuiOutputSlot(IGuiWrapper gui, ResourceLocation def, int x, int y) {
        this(SlotType.OUTPUT, gui, def, x, y);
    }

    public GuiOutputSlot(SlotType type, IGuiWrapper gui, ResourceLocation def, int x, int y) {
        super(type, gui, def, x, y);
    }

    public GuiOutputSlot(IGuiWrapper gui, ResourceLocation def, int x, int y, TileEntity tile) {
        this(SlotType.OUTPUT, gui, def, x, y, tile);
    }


    public GuiOutputSlot(SlotType type, IGuiWrapper gui, ResourceLocation def, int x, int y, TileEntity tile) {
        this(type, gui, def, x, y, new ISlotInfoHandler() {
            @Override
            public boolean getSlotCanTip() {
                return tile instanceof IMachineSlotTip tip && tip.getOuputSlot();
            }
        });
    }

    public GuiOutputSlot(IGuiWrapper gui, ResourceLocation def, int x, int y, ISlotInfoHandler handler) {
        this(SlotType.OUTPUT, gui, def, x, y, handler);
    }

    public GuiOutputSlot(SlotType type, IGuiWrapper gui, ResourceLocation def, int x, int y, ISlotInfoHandler handler) {
        super(type, gui, def, x, y, handler);
    }


}
