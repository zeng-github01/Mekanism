package mekanism.client.gui.element.slot;

import mekanism.client.gui.IGuiWrapper;
import mekanism.client.gui.element.GuiSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiNormalSlot extends GuiSlot {

    public GuiNormalSlot(IGuiWrapper gui, ResourceLocation def, int x, int y) {
        super(SlotType.NORMAL, gui, def, x, y);
    }

}
