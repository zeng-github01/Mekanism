package mekanism.client.gui;

import mekanism.api.TileNetworkList;
import mekanism.api.gas.GasStack;
import mekanism.client.gui.button.GuiDisableableButton;
import mekanism.client.gui.element.*;
import mekanism.client.gui.element.GuiProgress.IProgressInfoHandler;
import mekanism.client.gui.element.GuiProgress.ProgressBar;
import mekanism.client.gui.element.GuiUtils.TilingDirection;
import mekanism.client.gui.element.bar.GuiBar;
import mekanism.client.gui.element.tab.*;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.sound.SoundHandler;
import mekanism.common.InfuseStorage;
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
import java.util.List;


@SideOnly(Side.CLIENT)
public class GuiFactory extends GuiMekanismTile<TileEntityFactory> {

    private GuiButton infuserDumpButton = null;
    private GuiButton FactoryOldSortingButton;

    public GuiFactory(InventoryPlayer inventory, TileEntityFactory tile) {
        super(tile, new ContainerFactory(inventory, tile));
        ResourceLocation resource = getGuiLocation();

        int ymove = tileEntity.getRecipeType().getFuelType() == MachineFuelType.FARM ? 32 :
                tileEntity.getRecipeType().getFuelType() == MachineFuelType.CHANCE ? 21 :
                        tile.getRecipeType() == RecipeType.INFUSING || tile.getRecipeType().getFuelType() == MachineFuelType.ADVANCED || tileEntity.getRecipeType() == RecipeType.Dissolution? 11 :
                                tileEntity.getRecipeType() == RecipeType.Crystallizer ? 7 : 0;
        int xmove = tile.tier == FactoryTier.CREATIVE ? 72 : tile.tier == FactoryTier.ULTIMATE ? 34 : 0;
        xSize += xmove;
        ySize += ymove;

        if (tile.getRecipeType().getFuelType() == MachineFuelType.CHANCE || tile.getRecipeType().getFuelType() == MachineFuelType.FARM) {
            addGuiElement(new GuiPowerBarLong(this, tileEntity, resource, 164 + xmove, 15));
        } else {
            addGuiElement(new GuiPowerBar(this, tileEntity, resource, 164 + xmove, 15));
        }
        addGuiElement(new GuiRecipeType(this, tileEntity, resource, xmove, 0));
        addGuiElement(new GuiUpgradeTab(this, tileEntity, resource, xmove, 0));
        addGuiElement(new GuiSecurityTab(this, tileEntity, resource, xmove, 0));
        addGuiElement(new GuiRedstoneControl(this, tileEntity, resource, xmove, 0));

        int xPlayerOffset = tile.tier == FactoryTier.CREATIVE ? 36 : tile.tier == FactoryTier.ULTIMATE ? 19 : 0;

        addGuiElement(new GuiPlayerSlot(this, resource, 7 + xPlayerOffset, 83 + ymove));
        //slot
        //Energy
        addGuiElement(new GuiSlot(GuiSlot.SlotType.POWER, this, resource, 6, 12).with(GuiSlot.SlotOverlay.POWER));
        //Extra
        if (tileEntity.getRecipeType().getFuelType() == MachineFuelType.DOUBLE
                || tileEntity.getRecipeType() == RecipeType.INFUSING
                || tileEntity.GasAdvancedInputMachine()
                || tileEntity.GasInputMachine()) {
            addGuiElement(new GuiSlot(GuiSlot.SlotType.EXTRA, this, resource, 6, 56));
        }
        //Input and Output
        int Slotlocation = tileEntity.tier == FactoryTier.BASIC ? 54 : tileEntity.tier == FactoryTier.ADVANCED ? 34 : tileEntity.tier == FactoryTier.ELITE ? 28 : 26;
        int xDistance = tileEntity.tier == FactoryTier.BASIC ? 38 : tileEntity.tier == FactoryTier.ADVANCED ? 26 : 19;
        for (int i = 0; i < tileEntity.tier.processes; i++) {
            if (!tileEntity.NoItemInputMachine()) {
                addGuiElement(new GuiSlot(GuiSlot.SlotType.INPUT, this, resource, Slotlocation + (i * xDistance), 12));
            }
            if (!tileEntity.GasOutputMachine()) {
                addGuiElement(new GuiSlot(GuiSlot.SlotType.OUTPUT, this, resource, Slotlocation + (i * xDistance), 56));
            }

            if (tileEntity.getRecipeType().getFuelType() == MachineFuelType.FARM || tileEntity.getRecipeType().getFuelType() == MachineFuelType.CHANCE) {
                addGuiElement(new GuiSlot(GuiSlot.SlotType.OUTPUT, this, resource, Slotlocation + (i * xDistance), 77));
            }
        }
        addGuiElement(new GuiSideConfigurationTab(this, tileEntity, resource));
        addGuiElement(new GuiTransporterConfigTab(this, 32, tileEntity, resource));
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
        int yoffset = tileEntity.getRecipeType().getFuelType() == MachineFuelType.FARM ? 98 : 77;
        this.buttonList.add(infuserDumpButton = new GuiDisableableButton(1, guiLeft + left, guiTop + yoffset, 21, 10) {
            @Override
            public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
                if (tileEntity.getRecipeType() == RecipeType.INFUSING ||
                        tileEntity.GasAdvancedInputMachine() ||
                        tileEntity.GasInputMachine()) {
                    super.drawButton(mc, mouseX, mouseY, partialTicks);
                }
            }

            @Override
            public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                return (tileEntity.getRecipeType() == RecipeType.INFUSING ||
                        tileEntity.GasAdvancedInputMachine() ||
                        tileEntity.GasInputMachine())
                        && super.mousePressed(mc, mouseX, mouseY);
            }
        }.with(GuiDisableableButton.ImageOverlay.DUMP));
        buttonList.add(FactoryOldSortingButton = new GuiDisableableButton(2, guiLeft - 21, guiTop + 90, 18, 18).with(GuiDisableableButton.ImageOverlay.ROUND_ROBIN));
    }

    public void displayGauge(int xPos, int yPos, int sizeX, int sizeY, TextureAtlasSprite icon, int displayInt) {
        if (icon != null) {
            mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            GuiUtils.drawTiledSprite(guiLeft + xPos + 1, guiTop + yPos + 1, sizeY - 2, displayInt, sizeY - 2, icon, TilingDirection.DOWN_RIGHT);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRenderer.drawString(tileEntity.getName(), (xSize / 2) - (fontRenderer.getStringWidth(tileEntity.getName()) / 2), 4, 0x404040);
        int xOffset = tileEntity.tier == FactoryTier.CREATIVE ? 44 : tileEntity.tier == FactoryTier.ULTIMATE ? 27 : 8;
        fontRenderer.drawString(LangUtils.localize("container.inventory"), xOffset, (ySize - 93) + 2, 0x404040);
        int xAxis = mouseX - guiLeft;
        int yAxis = mouseY - guiTop;
        if (infuserDumpButton.isMouseOver()) {
            displayTooltip(LangUtils.localize("gui.remove"), xAxis, yAxis);
        } else if (FactoryOldSortingButton.isMouseOver()) {
            List<String> info = new ArrayList<>();
            info.add(LangUtils.localize("gui.factory.autoSort.old") + ":" + LangUtils.transOnOff(tileEntity.Factoryoldsorting));
            info.add(LangUtils.localize("gui.factory.autoSort.old.info"));
            info.add(LangUtils.localize("gui.factory.autoSort.old.info2"));
            displayTooltips(info, xAxis, yAxis);
        } else if (xAxis >= -21 && xAxis <= -3 && yAxis >= 116 && yAxis <= 134) {
            List<String> info = new ArrayList<>();
            boolean outslot = false;
            boolean outslot2 = false;
            boolean inputgas = false;
            boolean inputinfuse = false;
            boolean energy = tileEntity.getEnergy() < tileEntity.energyPerTick || tileEntity.getEnergy() == 0;
            for (int i = 0; i < tileEntity.tier.processes; i++) {
                if (tileEntity.inventory.get(5 + tileEntity.tier.processes + i).getCount() == tileEntity.inventory.get(5 + tileEntity.tier.processes + i).getMaxStackSize()) {
                    outslot = true;
                }
                if (tileEntity.inventory.get(5 + tileEntity.tier.processes * 2 + i).getCount() == tileEntity.inventory.get(5 + tileEntity.tier.processes * 2 + i).getMaxStackSize()) {
                    outslot2 = true;
                }
                if ((tileEntity.gasTank.getStored() == 0) && (tileEntity.inventory.get(5 + i).getCount() != 0) && tileEntity.GasAdvancedInputMachine()) {
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
            if (outslot2) {
                info.add(LangUtils.localize("gui.item_no_space"));
            }
            if (outslot || outslot2 || energy || inputgas || inputinfuse) {
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
        int Xgastank = tileEntity.tier == FactoryTier.BASIC ? 94 : tileEntity.tier == FactoryTier.ADVANCED ? 122 : tileEntity.tier == FactoryTier.ELITE ? 132 : tileEntity.tier == FactoryTier.ULTIMATE ? 170 : 208;
        int ygas = tileEntity.getRecipeType().getFuelType() == MachineFuelType.FARM ? 98 : 77;
        if (tileEntity.GasAdvancedInputMachine()) {
            addGuiElement(new GuiBar(() -> (tileEntity.gasTank.getGas() != null ? tileEntity.gasTank.getGas().getGas().getLocalizedName() + ": " + tileEntity.gasTank.getStored() : LangUtils.localize("gui.none")), this, getGuiLocation(), 7, ygas, xgas, 7));
        } else if (tileEntity.getRecipeType() == RecipeType.INFUSING) {
            addGuiElement(new GuiBar(() -> (tileEntity.infuseStored.getType() != null ? tileEntity.infuseStored.getType().getLocalizedName() + ": " + tileEntity.infuseStored.getAmount() : LangUtils.localize("gui.empty")), this, getGuiLocation(), 7, 77, xgas, 7));
        } else if (tileEntity.getRecipeType() == RecipeType.Crystallizer) {
            addGuiElement(new GuiBar(() -> (tileEntity.gasTank.getGas() != null ? tileEntity.gasTank.getGas().getGas().getLocalizedName() + ": " + tileEntity.gasTank.getStored() : LangUtils.localize("gui.none")), this, getGuiLocation(), Slotlocation, 12, Xgastank, 18));
        } else if (tileEntity.getRecipeType() == RecipeType.Dissolution) {
            addGuiElement(new GuiBar(() -> (tileEntity.gasTank.getGas() != null ? tileEntity.gasTank.getGas().getGas().getLocalizedName() + ": " + tileEntity.gasTank.getStored() : LangUtils.localize("gui.none")), this, getGuiLocation(), 7, ygas, xgas, 7));
            addGuiElement(new GuiBar(() -> (tileEntity.gasOutTank.getGas() != null ? tileEntity.gasOutTank.getGas().getGas().getLocalizedName() + ": " + tileEntity.gasOutTank.getStored() : LangUtils.localize("gui.none")), this, getGuiLocation(), Slotlocation, 56, Xgastank, 18));
        }

        if (tileEntity.GasAdvancedInputMachine()) {
            if ((int) tileEntity.getScaledGasLevel(xgas - 2) > 0) {
                GasStack gas = tileEntity.gasTank.getGas();
                if (gas != null) {
                    MekanismRenderer.color(gas);
                    displayGauge(7, ygas, xgas, 7, gas.getGas().getSprite(), (int) tileEntity.getScaledGasLevel(xgas - 2));
                    MekanismRenderer.resetColor();
                }
            }
        } else if (tileEntity.getRecipeType() == RecipeType.Crystallizer) {
            if ((int) tileEntity.getScaledGasLevel(Xgastank - 2) > 0) {
                GasStack gas = tileEntity.gasTank.getGas();
                if (gas != null) {
                    MekanismRenderer.color(gas);
                    displayGauge(Slotlocation, 12, Xgastank, 18, gas.getGas().getSprite(), (int) tileEntity.getScaledGasLevel(Xgastank - 2));
                    MekanismRenderer.resetColor();
                }
            }
        } else if (tileEntity.getRecipeType() == RecipeType.INFUSING) {
            if ((int) tileEntity.getScaledInfuseLevel(xgas - 2) > 0) {
                InfuseStorage storage = tileEntity.infuseStored;
                if (storage.getType() != null) {
                    displayGauge(7, ygas, xgas, 7, storage.getType().sprite, (int) tileEntity.getScaledInfuseLevel(xgas - 2));
                    MekanismRenderer.resetColor();
                }
            }
        } else if (tileEntity.getRecipeType() == RecipeType.Dissolution) {
            if ((int) tileEntity.getScaledGasLevel(Xgastank - 2) > 0) {
                GasStack gas = tileEntity.gasTank.getGas();
                if (gas != null) {
                    MekanismRenderer.color(gas);
                    displayGauge(7, ygas, xgas, 7, gas.getGas().getSprite(), (int) tileEntity.getScaledGasLevel(xgas - 2));
                    MekanismRenderer.resetColor();
                }
            }
            if ((int) tileEntity.getScaledGasOutlevel(Xgastank - 2) > 0) {
                GasStack gas = tileEntity.gasOutTank.getGas();
                if (gas != null) {
                    MekanismRenderer.color(gas);
                    displayGauge(Slotlocation, 56, Xgastank, 18, gas.getGas().getSprite(), (int) tileEntity.getScaledGasOutlevel(Xgastank - 2));
                    MekanismRenderer.resetColor();
                }
            }
        }

        for (int i = 0; i < tileEntity.tier.processes; i++) {
            boolean outslot = tileEntity.inventory.get(5 + tileEntity.tier.processes + i).getCount() == tileEntity.inventory.get(5 + tileEntity.tier.processes + i).getMaxStackSize();
            boolean outslot2 = tileEntity.inventory.get(5 + tileEntity.tier.processes * 2 + i).getCount() == tileEntity.inventory.get(5 + tileEntity.tier.processes * 2 + i).getMaxStackSize();
            boolean energy = tileEntity.getEnergy() < tileEntity.energyPerTick || tileEntity.getEnergy() == 0;
            boolean inputgas = (tileEntity.gasTank.getStored() == 0) && (tileEntity.inventory.get(5 + i).getCount() != 0) && tileEntity.GasAdvancedInputMachine();
            boolean inputinfuse = (tileEntity.infuseStored.getAmount() == 0) && (tileEntity.inventory.get(5 + i).getCount() != 0) && tileEntity.getRecipeType() == RecipeType.INFUSING;
            if (outslot) {
                mc.getTextureManager().bindTexture(MekanismUtils.getResource(ResourceType.SLOT, "Slot_Icon.png"));
                drawTexturedModalRect(guiLeft + (Slotlocation + (i * xDistance)), guiTop + 56, 158, 0, 18, 18);
            }
            if (outslot2) {
                mc.getTextureManager().bindTexture(MekanismUtils.getResource(ResourceType.SLOT, "Slot_Icon.png"));
                drawTexturedModalRect(guiLeft + (Slotlocation + (i * xDistance)), guiTop + 77, 158, 0, 18, 18);
            }
            if (inputgas || inputinfuse) {
                mc.getTextureManager().bindTexture(MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "Warning_Background.png"));
                drawTexturedModalRect(guiLeft + 8, guiTop + (tileEntity.getRecipeType().getFuelType() == MachineFuelType.FARM ? 78 + 21 : 78), 0, 0, xgas - 2, 5);
            }
            if (outslot || outslot2 || energy || inputgas || inputinfuse) {
                mc.getTextureManager().bindTexture(MekanismUtils.getResource(ResourceType.TAB, "Warning_Info.png"));
                drawTexturedModalRect(guiLeft - 26, guiTop + 112, 0, 0, 26, 26);
                addGuiElement(new GuiWarningInfo(this, getGuiLocation(), false));
            }
        }
        mc.getTextureManager().bindTexture(MekanismUtils.getResource(MekanismUtils.ResourceType.GUI, "State.png"));
        drawTexturedModalRect(guiLeft - 10, guiTop + 81, 6, 6, 8, 8);
        drawTexturedModalRect(guiLeft - 9, guiTop + 82, tileEntity.Factoryoldsorting ? 0 : 6, 0, 6, 6);
    }


    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException {
        super.mouseClicked(x, y, button);
        if (button == 0 || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            int xAxis = x - guiLeft;
            int yAxis = y - guiTop;
            int xOffset = tileEntity.tier == FactoryTier.CREATIVE ? 218 : tileEntity.tier == FactoryTier.ULTIMATE ? 180 : 146;
            int isfarm = tileEntity.getRecipeType().getFuelType() == MachineFuelType.FARM ? 21 : 0;
            if (xAxis > 8 && xAxis < xOffset && yAxis > 78 + isfarm && yAxis < 83 + isfarm) {
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
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button == this.infuserDumpButton) {
            TileNetworkList data = TileNetworkList.withContents(1);
            Mekanism.packetHandler.sendToServer(new TileEntityMessage(tileEntity, data));
            SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
        } else if (button == FactoryOldSortingButton) {
            TileNetworkList data = TileNetworkList.withContents(2);
            Mekanism.packetHandler.sendToServer(new TileEntityMessage(tileEntity, data));
            SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
        }
    }
}
