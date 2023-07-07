package mekanism.client.gui.robit;

import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.button.GuiButtonDisableableImage;
import mekanism.client.gui.element.GuiPlayerSlot;
import mekanism.common.Mekanism;
import mekanism.common.entity.EntityRobit;
import mekanism.common.network.PacketRobit.RobitMessage;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public abstract class GuiRobit extends GuiMekanism {

    protected final EntityRobit robit;
    private GuiButton mainButton;
    private GuiButton craftingButton;
    private GuiButton inventoryButton;
    private GuiButton smeltingButton;
    private GuiButton repairButton;

    protected GuiRobit(EntityRobit robit, Container container) {
        super(container);
        this.robit = robit;
        xSize += 25;
        addGuiElement(new GuiPlayerSlot(this,getGuiLocation()));
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        buttonList.add(mainButton = new GuiButtonDisableableImage(0, guiLeft + 179, guiTop + 10, 18, 18, 0, 18, -18, getGuiRoBitTab()));
        buttonList.add(craftingButton = new GuiButtonDisableableImage(1, guiLeft + 179, guiTop + 30, 18, 18, 0, 54, -18, getGuiRoBitTab()));
        buttonList.add(inventoryButton = new GuiButtonDisableableImage(2, guiLeft + 179, guiTop + 50, 18, 18, 0, 90, -18, getGuiRoBitTab()));
        buttonList.add(smeltingButton = new GuiButtonDisableableImage(3, guiLeft + 179, guiTop + 70, 18, 18, 0, 126, -18, getGuiRoBitTab()));
        buttonList.add(repairButton = new GuiButtonDisableableImage(4, guiLeft + 179, guiTop + 90, 18, 18, 0, 162, -18, getGuiRoBitTab()));
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) throws IOException {
        super.actionPerformed(guibutton);
        if (!shouldOpenGui(guibutton.id)) {
            //Don't do anything when the button is the same one as the one we are on
            return;
        }
        if (guibutton.id == mainButton.id) {
            Mekanism.packetHandler.sendToServer(new RobitMessage(robit.getEntityId(), 21));
        } else if (guibutton.id == craftingButton.id) {
            Mekanism.packetHandler.sendToServer(new RobitMessage(robit.getEntityId(), 22));
        } else if (guibutton.id == inventoryButton.id) {
            Mekanism.packetHandler.sendToServer(new RobitMessage(robit.getEntityId(), 23));
        } else if (guibutton.id == smeltingButton.id) {
            Mekanism.packetHandler.sendToServer(new RobitMessage(robit.getEntityId(), 24));
        } else if (guibutton.id == repairButton.id) {
            Mekanism.packetHandler.sendToServer(new RobitMessage(robit.getEntityId(), 25));
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        int xAxis = mouseX - guiLeft;
        int yAxis = mouseY - guiTop;
        if (mainButton.isMouseOver()){
            displayTooltip(LangUtils.localize("gui.robit"), xAxis, yAxis);
        }else if (craftingButton.isMouseOver()){
            displayTooltip(LangUtils.localize("gui.robit.crafting"), xAxis, yAxis);
        }else if (inventoryButton.isMouseOver()){
            displayTooltip(LangUtils.localize("gui.robit.inventory"), xAxis, yAxis);
        }else if (smeltingButton.isMouseOver()){
            displayTooltip(LangUtils.localize("gui.robit.smelting"), xAxis, yAxis);
        }else if (repairButton.isMouseOver()){
            displayTooltip(LangUtils.localize("gui.robit.repair"), xAxis, yAxis);
        }
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        return MekanismUtils.getResource(ResourceType.GUI, getBackgroundImage());
    }

    protected ResourceLocation getGuiRoBitTab() {
        return MekanismUtils.getResource(ResourceType.GUI_ELEMENT, "GuiRoBitTab.png");
    }


    protected abstract String getBackgroundImage();

    protected abstract boolean shouldOpenGui(int id);
}