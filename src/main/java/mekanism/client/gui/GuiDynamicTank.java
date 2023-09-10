package mekanism.client.gui;

import mekanism.client.gui.element.GuiContainerEditMode;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.GuiPlayerArmmorSlot;
import mekanism.client.gui.element.GuiPlayerSlot;
import mekanism.client.gui.element.gauge.GuiGauge;
import mekanism.client.gui.element.gauge.GuiNumberGauge;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.inventory.container.ContainerDynamicTank;
import mekanism.common.tile.TileEntityDynamicTank;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDynamicTank extends GuiMekanismTile<TileEntityDynamicTank> {

    public GuiDynamicTank(InventoryPlayer inventory, TileEntityDynamicTank tile) {
        super(tile, new ContainerDynamicTank(inventory, tile));
        ResourceLocation resource = getGuiLocation();
        addGuiElement(new GuiContainerEditMode(this, tileEntity, resource));
        addGuiElement(new GuiInnerScreen(this, resource, 49, 21, 84, 46));
        addGuiElement(new GuiPlayerArmmorSlot(this, resource, -26, 62, true));
        addGuiElement(new GuiPlayerSlot(this, resource));
        addGuiElement(new GuiNumberGauge(new GuiNumberGauge.INumberInfoHandler() {
            @Override
            public TextureAtlasSprite getIcon() {
                return MekanismRenderer.getFluidTexture(tileEntity.structure != null ? tileEntity.structure.fluidStored : null, MekanismRenderer.FluidType.STILL);
            }

            @Override
            public double getLevel() {
                if (tileEntity.structure != null && tileEntity.structure.fluidStored != null) {
                    return tileEntity.structure.fluidStored.amount;
                } else {
                    return 0;
                }
            }

            @Override
            public double getMaxLevel() {
                if (tileEntity.structure != null && tileEntity.structure.fluidStored != null) {
                    return tileEntity.clientCapacity;
                } else {
                    return 0;
                }
            }

            @Override
            public String getText(double level) {
                return tileEntity.structure != null ? (tileEntity.structure.fluidStored != null ? LangUtils.localizeFluidStack(tileEntity.structure.fluidStored) + ": " + tileEntity.structure.fluidStored.amount + "mB" : LangUtils.localize("gui.empty")) : "";
            }
        }, GuiGauge.Type.MEDIUM, this, resource, 7, 13));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(tileEntity.getName(), (xSize / 2) - (fontRenderer.getStringWidth(tileEntity.getName()) / 2), 4, 0x404040);
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 94) + 2, 0x404040);
        FluidStack fluidStored = tileEntity.structure != null ? tileEntity.structure.fluidStored : null;
        renderScaledText(fluidStored != null ? LangUtils.localizeFluidStack(fluidStored) + ":" : LangUtils.localize("gui.noFluid"), 53, fluidStored != null ? 26 : 35, 0xFF3CFE9A, 74);
        if (fluidStored != null) {
            fontRenderer.drawString(fluidStored.amount + "mB", 53, 35, 0xFF3CFE9A);
        }
        fontRenderer.drawString(LangUtils.localize("gui.capacity") + ": ", 53, 44, 0xFF3CFE9A);
        fontRenderer.drawString(tileEntity.clientCapacity  + "mB",53,53,0xFF3CFE9A);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(int xAxis, int yAxis) {
        super.drawGuiContainerBackgroundLayer(xAxis, yAxis);
        mc.getTextureManager().bindTexture(MekanismUtils.getResource(ResourceType.GUI, "Other_Icon.png"));
        drawTexturedModalRect(guiLeft + 141, guiTop + 15, 0, 16, 26, 57);
        drawTexturedModalRect(guiLeft + 150, guiTop + 39, 13, 0, 8, 9);
    }

}
