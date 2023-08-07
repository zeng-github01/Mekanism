package mekanism.client.gui;

import mekanism.api.TileNetworkList;
import mekanism.api.gas.GasStack;
import mekanism.api.infuse.InfuseType;
import mekanism.client.gui.button.GuiDisableableButton;
import mekanism.client.gui.element.*;
import mekanism.client.gui.element.GuiProgress.IProgressInfoHandler;
import mekanism.client.gui.element.GuiProgress.ProgressBar;
import mekanism.client.gui.element.bar.GuiBar;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiFactory extends GuiMekanismTile<TileEntityFactory> {

    private GuiButton infuserDumpButton = null;

    public GuiFactory(InventoryPlayer inventory, TileEntityFactory tile) {
        super(tile, new ContainerFactory(inventory, tile));
        ResourceLocation resource = getGuiLocation();

        int ymobe = tile.getRecipeType() == RecipeType.INFUSING || tile.getRecipeType().getFuelType() == MachineFuelType.ADVANCED ? 11 : 0;
        int xmove = tile.tier == FactoryTier.CREATIVE ? 72 : tile.tier == FactoryTier.ULTIMATE ? 34 : 0;
        xSize += xmove;
        ySize += ymobe;

        addGuiElement(new GuiPowerBar(this, tileEntity, resource, 164 + xmove, 15));
        addGuiElement(new GuiRecipeType(this, tileEntity, resource, xmove, 0));
        addGuiElement(new GuiUpgradeTab(this, tileEntity, resource, xmove, 0));
        addGuiElement(new GuiSecurityTab(this, tileEntity, resource, xmove, 0));
        addGuiElement(new GuiRedstoneControl(this, tileEntity, resource, xmove, 0));

        int xPlayerOffset = tile.tier == FactoryTier.CREATIVE ? 36 : tile.tier == FactoryTier.ULTIMATE ? 19 : 0;

        addGuiElement(new GuiPlayerSlot(this, resource, 7 + xPlayerOffset, 83 + ymobe));
        //slot
        //Energy
        addGuiElement(new GuiSlot(GuiSlot.SlotType.POWER, this, resource, 6, 12).with(GuiSlot.SlotOverlay.POWER));
        //Extra
        if (tileEntity.getRecipeType().getFuelType() == MachineFuelType.CHANCE
                || tileEntity.getRecipeType().getFuelType() == MachineFuelType.DOUBLE
                || tileEntity.getRecipeType() == RecipeType.INFUSING
                || tileEntity.getRecipeType().getFuelType() == MachineFuelType.ADVANCED) {
            addGuiElement(new GuiSlot(GuiSlot.SlotType.EXTRA, this, resource, 6, 56));
        }
        //Input and Output
        int Slotlocation = tileEntity.tier == FactoryTier.BASIC ? 54 : tileEntity.tier == FactoryTier.ADVANCED ? 34 : tileEntity.tier == FactoryTier.ELITE ? 28 : 26;
        int xDistance = tileEntity.tier == FactoryTier.BASIC ? 38 : tileEntity.tier == FactoryTier.ADVANCED ? 26 : 19;
        for (int i = 0; i < tileEntity.tier.processes; i++) {
            addGuiElement(new GuiSlot(GuiSlot.SlotType.INPUT, this, resource, Slotlocation + (i * xDistance), 12));
            addGuiElement(new GuiSlot(GuiSlot.SlotType.OUTPUT, this, resource, Slotlocation + (i * xDistance), 56));
        }
        addGuiElement(new GuiSideConfigurationTab(this, tileEntity, resource));
        addGuiElement(new GuiTransporterConfigTab(this, 34, tileEntity, resource));
        addGuiElement(new GuiSortingTab(this, tileEntity, resource));


        addGuiElement(new GuiEnergyInfo(() -> {
            String multiplier = MekanismUtils.getEnergyDisplay(tileEntity.energyPerTick);
            return Arrays.asList(LangUtils.localize("gui.using") + ": " + multiplier + "/t",
                    LangUtils.localize("gui.needed") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxEnergy() - tileEntity.getEnergy()));
        }, this, resource));
        int xOffset = tileEntity.tier == FactoryTier.BASIC ? 57 : tileEntity.tier == FactoryTier.ADVANCED ? 37 : tileEntity.tier == FactoryTier.ELITE ? 31 : 29;
        for (int i = 0; i < tileEntity.tier.processes; i++) {
            int cacheIndex = i;
            int xPos = xOffset + (i * xDistance);
            addGuiElement(new GuiProgress(new IProgressInfoHandler() {
                @Override
                public double getProgress() {
                    return tileEntity.getScaledProgress(1, cacheIndex);
                }
            }, ProgressBar.DOWN, this, resource, xPos, 33));
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        int left = tileEntity.tier == FactoryTier.CREATIVE ? 220 : tileEntity.tier == FactoryTier.ULTIMATE ? 182 : 148;
        this.buttonList.add(infuserDumpButton = new GuiDisableableButton(1, guiLeft + left, guiTop + 77, 21, 10) {
            @Override
            public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
                if (tileEntity.getRecipeType() == RecipeType.INFUSING || tileEntity.getRecipeType().getFuelType() == MachineFuelType.ADVANCED) {
                    super.drawButton(mc, mouseX, mouseY, partialTicks);
                }
            }

            @Override
            public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                return (tileEntity.getRecipeType() == RecipeType.INFUSING || tileEntity.getRecipeType().getFuelType() == MachineFuelType.ADVANCED)
                        && super.mousePressed(mc, mouseX, mouseY);
            }
        }.with(GuiDisableableButton.ImageOverlay.DUMP));
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
        int xOffset = tileEntity.tier == FactoryTier.CREATIVE ? 44 : tileEntity.tier == FactoryTier.ULTIMATE ? 27 : 8;
        fontRenderer.drawString(LangUtils.localize("container.inventory"), xOffset, (ySize - 93) + 2, 0x404040);
        int xAxis = mouseX - guiLeft;
        int yAxis = mouseY - guiTop;
        int xgas = tileEntity.tier == FactoryTier.CREATIVE ? 219 : tileEntity.tier == FactoryTier.ULTIMATE ? 181 : 147;
        if (infuserDumpButton.isMouseOver()) {
            displayTooltip(LangUtils.localize("gui.remove"), xAxis, yAxis);
        } else if (xAxis >= -21 && xAxis <= -3 && yAxis >= 116 && yAxis <= 134) {
            List<String> info = new ArrayList<>();
            boolean outslot = false;
            boolean inputgas = false;
            boolean inputinfuse = false;
            boolean energy = tileEntity.getEnergy() < tileEntity.energyPerTick || tileEntity.getEnergy() == 0;
            for (int i = 0; i < tileEntity.tier.processes; i++) {
                if (tileEntity.inventory.get(5 + tileEntity.tier.processes + i).getCount() == tileEntity.inventory.get(5 + tileEntity.tier.processes + i).getMaxStackSize()) {
                    outslot = true;
                }
                if ((tileEntity.gasTank.getStored() == 0) && (tileEntity.inventory.get(5 + i).getCount() != 0) && tileEntity.getRecipeType().getFuelType() == MachineFuelType.ADVANCED) {
                    inputgas = true;
                }
                if ((tileEntity.infuseStored.getAmount() == 0) && (tileEntity.inventory.get(5 + i).getCount() != 0) && tileEntity.getRecipeType() == RecipeType.INFUSING) {
                    inputinfuse = true;
                }
            }
            if (energy) {
                info.add(LangUtils.localize("gui.no_energy"));
            }
            if (inputinfuse) {
                info.add(LangUtils.localize("gui.infuse_no_item"));
            }
            if (inputgas) {
                info.add(LangUtils.localize("gui.no_gas"));
            }
            if (outslot) {
                info.add(LangUtils.localize("gui.item_no_space"));
            }
            if (outslot || energy || inputgas || inputinfuse) {
                displayTooltips(info, xAxis, yAxis);
            }

        }
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(int xAxis, int yAxis) {
        super.drawGuiContainerBackgroundLayer(xAxis, yAxis);

        int xDistance = tileEntity.tier == FactoryTier.BASIC ? 38 : tileEntity.tier == FactoryTier.ADVANCED ? 26 : 19;
        int Slotlocation = tileEntity.tier == FactoryTier.BASIC ? 54 : tileEntity.tier == FactoryTier.ADVANCED ? 34 : tileEntity.tier == FactoryTier.ELITE ? 28 : 26;
        int xgas = tileEntity.tier == FactoryTier.CREATIVE ? 212 : tileEntity.tier == FactoryTier.ULTIMATE ? 174 : 140;
        if (tileEntity.getRecipeType().getFuelType() == MachineFuelType.ADVANCED) {
            addGuiElement(new GuiBar(() -> (tileEntity.gasTank.getGas() != null ? tileEntity.gasTank.getGas().getGas().getLocalizedName() + ": " + tileEntity.gasTank.getStored() : LangUtils.localize("gui.none")), this, getGuiLocation(), 7, 77, xgas, 7));
        } else if (tileEntity.getRecipeType() == RecipeType.INFUSING) {
            addGuiElement(new GuiBar(() -> (tileEntity.infuseStored.getType() != null ? tileEntity.infuseStored.getType().getLocalizedName() + ": " + tileEntity.infuseStored.getAmount() : LangUtils.localize("gui.empty")), this, getGuiLocation(), 7, 77, xgas, 7));
        }

        if (tileEntity.getRecipeType().getFuelType() == MachineFuelType.ADVANCED) {
            if ((int) tileEntity.getScaledGasLevel(xgas - 2) > 0) {
                GasStack gas = tileEntity.gasTank.getGas();
                if (gas != null) {
                    MekanismRenderer.color(gas);
                    displayGauge(8, 78, (int) tileEntity.getScaledGasLevel(xgas - 2), 5, gas.getGas().getSprite());
                    MekanismRenderer.resetColor();
                }
            }
        } else if (tileEntity.getRecipeType() == RecipeType.INFUSING) {
            if ((int) tileEntity.getScaledInfuseLevel(xgas - 2) > 0) {
                MekanismRenderer.resetColor();
                displayGauge(8, 78, (int) tileEntity.getScaledInfuseLevel(xgas - 2), 5, tileEntity.infuseStored.getType().sprite);
            }
        }
        for (int i = 0; i < tileEntity.tier.processes; i++) {
            boolean outslot = tileEntity.inventory.get(5 + tileEntity.tier.processes + i).getCount() == tileEntity.inventory.get(5 + tileEntity.tier.processes + i).getMaxStackSize();
            boolean energy = tileEntity.getEnergy() < tileEntity.energyPerTick || tileEntity.getEnergy() == 0;
            boolean inputgas = (tileEntity.gasTank.getStored() == 0) && (tileEntity.inventory.get(5 + i).getCount() != 0) && tileEntity.getRecipeType().getFuelType() == MachineFuelType.ADVANCED;
            boolean inputinfuse = (tileEntity.infuseStored.getAmount() == 0) && (tileEntity.inventory.get(5 + i).getCount() != 0) && tileEntity.getRecipeType() == RecipeType.INFUSING;
            if (outslot) {
                mc.getTextureManager().bindTexture(MekanismUtils.getResource(ResourceType.SLOT, "Slot_Icon.png"));
                drawTexturedModalRect(guiLeft + (Slotlocation + (i * xDistance)), guiTop + 56, 158, 0, 18, 18);
            }
            if (inputgas || inputinfuse) {
                mc.getTextureManager().bindTexture(MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "Warning_Background.png"));
                drawTexturedModalRect(guiLeft + 8, guiTop + 78, 0, 0, xgas - 2, 5);
            }
            if (outslot || energy || inputgas || inputinfuse) {
                mc.getTextureManager().bindTexture(MekanismUtils.getResource(ResourceType.TAB, "Warning_Info.png"));
                drawTexturedModalRect(guiLeft - 26, guiTop + 112, 0, 0, 26, 26);
                addGuiElement(new GuiWarningInfo(this, getGuiLocation(), false));
            }
        }
    }


    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException {
        super.mouseClicked(x, y, button);
        if (button == 0 || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            int xAxis = x - guiLeft;
            int yAxis = y - guiTop;
            int xOffset = tileEntity.tier == FactoryTier.CREATIVE ? 218 : tileEntity.tier == FactoryTier.ULTIMATE ? 180 : 146;
            if (xAxis > 8 && xAxis < xOffset && yAxis > 78 && yAxis < 83) {
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
        return MekanismUtils.getResource(ResourceType.GUI, "Null.png");
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