package mekanism.client.gui;

import mekanism.api.TileNetworkList;
import mekanism.api.gas.GasStack;
import mekanism.api.infuse.InfuseType;
import mekanism.client.gui.button.GuiButtonDisableableImage;
import mekanism.client.gui.element.*;
import mekanism.client.gui.element.tab.*;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.base.IFactory.MachineFuelType;
import mekanism.common.base.IFactory.RecipeType;
import mekanism.common.inventory.container.ContainerFactory;
import mekanism.common.item.ItemGaugeDropper;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tier.FactoryTier;
import mekanism.common.tile.TileEntityFactory;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.Arrays;

@SideOnly(Side.CLIENT)
public class GuiFactory extends GuiMekanismTile<TileEntityFactory> {

    private GuiButton infuserDumpButton = null;
    public GuiFactory(InventoryPlayer inventory, TileEntityFactory tile) {
        super(tile, new ContainerFactory(inventory, tile));
        ResourceLocation resource = getGuiLocation();
        if (GuiFactory.this.tileEntity.getRecipeType() == RecipeType.INFUSING || GuiFactory.this.tileEntity.getRecipeType().getFuelType() == MachineFuelType.ADVANCED) {
            ySize += 11;
        }
        if (tile.tier == FactoryTier.BASIC || tile.tier == FactoryTier.ADVANCED || tile.tier == FactoryTier.ELITE){
            addGuiElement(new GuiPowerBar(this, tileEntity, resource,164,15));
            addGuiElement(new GuiRecipeType(this, tileEntity, resource,0,0));
            addGuiElement(new GuiUpgradeTab(this, tileEntity, resource,0,0));
            addGuiElement(new GuiSecurityTab(this, tileEntity, resource,0,0));
            addGuiElement(new GuiRedstoneControl(this, tileEntity, resource,0,0));
        }else {
            addGuiElement(new GuiPowerBar(this, tileEntity, resource,164,15));
            addGuiElement(new GuiRecipeType(this, tileEntity, resource,0,0));
            addGuiElement(new GuiUpgradeTab(this, tileEntity, resource,0,0));
            addGuiElement(new GuiSecurityTab(this, tileEntity, resource,0,0));
            addGuiElement(new GuiRedstoneControl(this, tileEntity, resource,0,0));
        }
        //slot
        //Energy
        addGuiElement(new GuiSlot(GuiSlot.SlotType.POWER, this, resource, 6, 12).with(GuiSlot.SlotOverlay.POWER));

        //Extra
        if (tileEntity.getRecipeType().getFuelType() == MachineFuelType.CHANCE
                || tileEntity.getRecipeType().getFuelType() == MachineFuelType.DOUBLE
                || tileEntity.getRecipeType() == RecipeType.INFUSING
                || tileEntity.getRecipeType().getFuelType() == MachineFuelType.ADVANCED){
            addGuiElement(new GuiSlot(GuiSlot.SlotType.EXTRA, this, resource, 6, 56));
        }

        //Input and Output
        if (tile.tier == FactoryTier.BASIC){
            for (int i = 0; i < tileEntity.tier.processes; i++) {
                addGuiElement(new GuiSlot(GuiSlot.SlotType.INPUT, this, resource, 54 + (i * 38), 12));
                addGuiElement(new GuiSlot(GuiSlot.SlotType.OUTPUT, this, resource, 54 + (i * 38), 56));
            }
        }else if (tile.tier == FactoryTier.ADVANCED){
            for (int i = 0; i < tileEntity.tier.processes; i++) {
                addGuiElement(new GuiSlot(GuiSlot.SlotType.INPUT, this, resource, 34 + (i * 26), 12));
                addGuiElement(new GuiSlot(GuiSlot.SlotType.OUTPUT, this, resource, 34 + (i * 26), 56));
            }
        }else if (tile.tier == FactoryTier.ELITE){
            for (int i = 0; i < tileEntity.tier.processes; i++) {
                addGuiElement(new GuiSlot(GuiSlot.SlotType.INPUT, this, resource, 28 + (i * 19), 12));
                addGuiElement(new GuiSlot(GuiSlot.SlotType.OUTPUT, this, resource, 28 + (i * 19), 56));
            }
        }

        addGuiElement(new GuiSideConfigurationTab(this, tileEntity, resource));
        addGuiElement(new GuiTransporterConfigTab(this, 34, tileEntity, resource));
        addGuiElement(new GuiSortingTab(this, tileEntity, resource));
        addGuiElement(new GuiEnergyInfo(() -> {
            String multiplier = MekanismUtils.getEnergyDisplay(tileEntity.lastUsage);
            return Arrays.asList(LangUtils.localize("gui.using") + ": " + multiplier + "/t",
                  LangUtils.localize("gui.needed") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxEnergy() - tileEntity.getEnergy()));
        }, this, resource));
    }


    @Override
    public void initGui() {
        super.initGui();
        int left = 0;
        if (tileEntity.tier == FactoryTier.BASIC || tileEntity.tier == FactoryTier.ADVANCED || tileEntity.tier == FactoryTier.ELITE){
            left += 148;
        }
        this.buttonList.add(this.infuserDumpButton = new GuiButtonDisableableImage(1, guiLeft + left, this.guiTop + 77, 21, 10, 37, 177, -10, MekanismUtils.getResource(ResourceType.GUI, "GuiBlankIcon.png")){
            @Override
            public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
                if (GuiFactory.this.tileEntity.getRecipeType() == RecipeType.INFUSING || GuiFactory.this.tileEntity.getRecipeType().getFuelType() == MachineFuelType.ADVANCED) {
                    super.drawButton(mc, mouseX, mouseY, partialTicks);
                }
            }

            @Override
            public boolean mousePressed(Minecraft mc, int mouseX, int mouseY ) {
                return (GuiFactory.this.tileEntity.getRecipeType() == RecipeType.INFUSING || GuiFactory.this.tileEntity.getRecipeType().getFuelType() == MachineFuelType.ADVANCED) && super.mousePressed(mc, mouseX, mouseY);
            }
        });
    }

    public void displayGauge(int xPos, int yPos, int sizeX, int sizeY, TextureAtlasSprite icon) {
        if (icon != null) {
            mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            drawTexturedModalRect(guiLeft + xPos, guiTop + yPos, icon, sizeX, sizeY);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(tileEntity.getName(), (xSize / 2) - (fontRenderer.getStringWidth(tileEntity.getName()) / 2), 4, 0x404040);
        fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 93) + 2, 0x404040);
        int xAxis = mouseX - guiLeft;
        int yAxis = mouseY - guiTop;
        if (infuserDumpButton.isMouseOver()){
            displayTooltip(LangUtils.localize("gui.remove"), xAxis, yAxis);
        } else if (xAxis >= 8 && xAxis <= 147 && yAxis >= 78 && yAxis <= 83) {
            if (tileEntity.getRecipeType().getFuelType() == MachineFuelType.ADVANCED) {
                GasStack gasStack = tileEntity.gasTank.getGas();
                displayTooltip(gasStack != null ? gasStack.getGas().getLocalizedName() + ": " + tileEntity.gasTank.getStored() : LangUtils.localize("gui.none"), xAxis, yAxis);
            } else if (tileEntity.getRecipeType() == RecipeType.INFUSING) {
                InfuseType type = tileEntity.infuseStored.getType();
                displayTooltip(type != null ? type.getLocalizedName() + ": " + tileEntity.infuseStored.getAmount() : LangUtils.localize("gui.empty"), xAxis, yAxis);
            }
        }
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }


    @Override
        protected void drawGuiContainerBackgroundLayer(int xAxis, int yAxis) {
        super.drawGuiContainerBackgroundLayer(xAxis, yAxis);
        int xOffset = tileEntity.tier == FactoryTier.BASIC ? 59 : tileEntity.tier == FactoryTier.ADVANCED ? 39 : 33;
        int xDistance = tileEntity.tier == FactoryTier.BASIC ? 38 : tileEntity.tier == FactoryTier.ADVANCED ? 26 : 19;
        for (int i = 0; i < tileEntity.tier.processes; i++) {
            int xPos = xOffset + (i * xDistance);
            drawTexturedModalRect(guiLeft + xPos, guiTop + 33, 185, 52, 8, 20);
            int displayInt = tileEntity.getScaledProgress(20, i);
            drawTexturedModalRect(guiLeft + xPos, guiTop + 33, 176, 52, 8, displayInt);
        }
        if (tileEntity.getRecipeType().getFuelType() == MachineFuelType.ADVANCED || tileEntity.getRecipeType() == RecipeType.INFUSING) {
            drawTexturedModalRect(guiLeft + 7, guiTop + 77, 0, 179, 140, 7);
        }
        if (tileEntity.getRecipeType().getFuelType() == MachineFuelType.ADVANCED) {
            if (tileEntity.getScaledGasLevel(138) > 0) {
                GasStack gas = tileEntity.gasTank.getGas();
                if (gas != null) {
                    MekanismRenderer.color(gas);
                    displayGauge(8, 78, tileEntity.getScaledGasLevel(138), 5, gas.getGas().getSprite());
                    MekanismRenderer.resetColor();
                }
            }
        } else if (tileEntity.getRecipeType() == RecipeType.INFUSING) {
            if (tileEntity.getScaledInfuseLevel(138) > 0) {
                MekanismRenderer.resetColor();
                displayGauge(8, 78, tileEntity.getScaledInfuseLevel(138), 5, tileEntity.infuseStored.getType().sprite);
            }
        }
    }


    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException {
        super.mouseClicked(x, y, button);
        if (button == 0 || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            int xAxis = x - guiLeft;
            int yAxis = y - guiTop;
            if (xAxis > 8 && xAxis < 146 && yAxis > 78 && yAxis < 83) {
                ItemStack stack = mc.player.inventory.getItemStack();
                if (!stack.isEmpty() && stack.getItem() instanceof ItemGaugeDropper) {
                    TileNetworkList data = TileNetworkList.withContents(1);
                    Mekanism.packetHandler.sendToServer(new TileEntityMessage(tileEntity, data));
                    SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
                }
            }
        }
    }

    @Override
    protected ResourceLocation getGuiLocation() {
        if (tileEntity.getRecipeType().getFuelType() == MachineFuelType.ADVANCED || tileEntity.getRecipeType() == RecipeType.INFUSING){
            return MekanismUtils.getResource(ResourceType.GUI_FACTORY, "Infused.png");
        }else return MekanismUtils.getResource(ResourceType.GUI_FACTORY, "Default.png");
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button == this.infuserDumpButton) {
            TileNetworkList data = TileNetworkList.withContents(1);
            Mekanism.packetHandler.sendToServer(new TileEntityMessage(tileEntity, data));
            SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
        }
    }
}