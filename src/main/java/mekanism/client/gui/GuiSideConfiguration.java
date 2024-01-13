package mekanism.client.gui;

import mekanism.api.Coord4D;
import mekanism.api.RelativeSide;
import mekanism.api.transmitters.TransmissionType;
import mekanism.client.Utils.ClientUtil;
import mekanism.client.gui.button.GuiDisableableButton;
import mekanism.client.gui.button.GuiSideDataButton;
import mekanism.client.gui.element.GuiInnerScreen;
import mekanism.client.gui.element.tab.GuiConfigTypeTab;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.SideData;
import mekanism.common.base.ISideConfiguration;
import mekanism.common.inventory.container.ContainerNull;
import mekanism.common.network.PacketConfigurationUpdate.ConfigurationPacket;
import mekanism.common.network.PacketConfigurationUpdate.ConfigurationUpdateMessage;
import mekanism.common.network.PacketSimpleGui.SimpleGuiMessage;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.prefab.TileEntityContainerBlock;
import mekanism.common.util.LangUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class GuiSideConfiguration extends GuiMekanismTile<TileEntityContainerBlock> {

    private Map<Integer, GuiPos> slotPosMap = new HashMap<>();

    private List<Pair<Integer, Block>> blockList = new ArrayList<>();

    private ISideConfiguration configurable;
    private TransmissionType currentType;
    private List<GuiConfigTypeTab> configTabs = new ArrayList<>();
    private List<GuiSideDataButton> sideDataButtons = new ArrayList<>();
    private GuiDisableableButton backButton;
    private GuiDisableableButton autoEjectButton;
    private GuiDisableableButton clearButton;
    private int buttonID = 0;

    private int currentLayer;

    public GuiSideConfiguration(EntityPlayer player, ISideConfiguration tile) {
        super((TileEntityContainerBlock) tile, new ContainerNull(player, (TileEntityContainerBlock) tile));
        xSize = 156;
        ySize = 135;
        configurable = tile;
        ResourceLocation resource = getGuiLocation();
        for (TransmissionType type : configurable.getConfig().getTransmissions()) {
            GuiConfigTypeTab tab = new GuiConfigTypeTab(this, type, resource);
            addGuiElement(tab);
            configTabs.add(tab);
        }
        currentType = getTopTransmission();
        updateTabs();
        slotPosMap.put(0, new GuiPos(68, 92, RelativeSide.BOTTOM));
        slotPosMap.put(1, new GuiPos(68, 46, RelativeSide.TOP));
        slotPosMap.put(2, new GuiPos(68, 69, RelativeSide.FRONT));
        slotPosMap.put(3, new GuiPos(45, 92, RelativeSide.BACK));
        slotPosMap.put(4, new GuiPos(45, 69, RelativeSide.LEFT));
        slotPosMap.put(5, new GuiPos(91, 69, RelativeSide.RIGHT));
        addGuiElement(new GuiInnerScreen(this, resource, 41, 25, 74, 12));
        currentLayer = Math.max(0, blockList.size() - 1);
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        buttonList.add(backButton = new GuiDisableableButton(buttonID++, guiLeft + 6, guiTop + 6, 14).with(GuiDisableableButton.ImageOverlay.BACK));
        buttonList.add(autoEjectButton = new GuiDisableableButton(buttonID++, guiLeft + 136, guiTop + 6, 14).with(GuiDisableableButton.ImageOverlay.AUTO_EJECT));
        for (int i = 0; i < slotPosMap.size(); i++) {
            GuiPos guiPos = slotPosMap.get(i);
            EnumFacing facing = EnumFacing.byIndex(i);
            GuiSideDataButton button = new GuiSideDataButton(buttonID++, guiLeft + guiPos.xPos, guiTop + guiPos.yPos, i, () -> configurable.getConfig().getOutput(currentType, facing), () -> configurable.getConfig().getOutput(currentType, facing).color);
            buttonList.add(button);
            sideDataButtons.add(button);
        }
        buttonList.add(clearButton = new GuiDisableableButton(buttonID++, guiLeft + 136, guiTop + 95, 14).with(GuiDisableableButton.ImageOverlay.CLEAR_SIDES));
    }

    @Override
    protected void actionPerformed(GuiButton guibutton) throws IOException {
        super.actionPerformed(guibutton);
        TileEntity tile = (TileEntity) configurable;
        if (guibutton.id == backButton.id) {
            int guiId = Mekanism.proxy.getGuiId(tile.getBlockType(), tile.getBlockMetadata());
            Mekanism.packetHandler.sendToServer(new SimpleGuiMessage(Coord4D.get(tile), 0, guiId));
        } else if (guibutton.id == autoEjectButton.id) {
            Mekanism.packetHandler.sendToServer(new ConfigurationUpdateMessage(ConfigurationPacket.EJECT, Coord4D.get(tile), 0, 0, currentType));
        } else if (guibutton.id == clearButton.id) {
            for (int i = 0; i < slotPosMap.size(); i++) {
                Mekanism.packetHandler.sendToServer(new ConfigurationUpdateMessage(ConfigurationPacket.SIDE_DATA, Coord4D.get(tile), 2, i, currentType));
            }
        } else {
            for (GuiSideDataButton button : sideDataButtons) {
                if (guibutton.id == button.id) {
                    Mekanism.packetHandler.sendToServer(new ConfigurationUpdateMessage(ConfigurationPacket.SIDE_DATA, Coord4D.get(tile),
                            Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 2 : 0, button.getSlotPosMapIndex(), currentType));
                    break;
                }
            }
        }
    }

    public TransmissionType getTopTransmission() {
        return configurable.getConfig().getTransmissions().get(0);
    }

    public void setCurrentType(TransmissionType type) {
        currentType = type;
    }

    public void updateTabs() {
        int rendered = 0;
        for (GuiConfigTypeTab tab : configTabs) {
            tab.setVisible(currentType != tab.getTransmissionType());
            if (tab.isVisible()) {
                tab.setLeft(rendered >= 0 && rendered <= 3);
                tab.setY(2 + ((rendered % 4) * (26 + 2)));
            }
            rendered++;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String title = currentType.localize() + " " + LangUtils.localize("gui.config");
        fontRenderer.drawString(title, (xSize / 2) - (fontRenderer.getStringWidth(title) / 2), 5, 0x404040);
        if (configurable.getConfig().canEject(currentType)) {
            fontRenderer.drawString(LangUtils.localize("gui.eject") + ": " + LangUtils.transOnOff(configurable.getConfig().isEjecting(currentType)), 43, 27, 0xFF3CFE9A);
        } else {
            fontRenderer.drawString(LangUtils.localize("gui.noEject"), 43, 27, 0xFF3CFE9A);
        }
        String slots = LangUtils.localize("gui.slots");
        fontRenderer.drawString(slots, (xSize / 2) - (fontRenderer.getStringWidth(slots) / 2), 120, 0x787878);
        int xAxis = mouseX - guiLeft;
        int yAxis = mouseY - guiTop;
        for (GuiSideDataButton button : sideDataButtons) {
            if (button.isMouseOver()) {
                SideData data = button.getSideData();
                if (data != TileComponentConfig.EMPTY) {
                    List<String> info = new ArrayList<>();
                    for (int i = 0; i < slotPosMap.size(); i++) {
                        GuiPos guiPos = slotPosMap.get(i);
                        String FacingName = guiPos.FacingName.getTranslationKey();
                        if (button.getSlotPosMapIndex() == i) {
                            info.add(FacingName);
                        }
                    }
                    info.add(data.color + data.localize());

                    for (int i = 0; i < slotPosMap.size(); i++) {
                        int layer = currentLayer + (i);
                        if (0 <= layer && layer < blockList.size()) {
                            Pair<Integer, Block> integerBlockPair = blockList.get(layer);
                            ItemStack nameStack = new ItemStack(integerBlockPair.getRight(), 1, integerBlockPair.getLeft());
                            if (integerBlockPair.getRight() != Blocks.AIR) { //Don't show the name of the air
                                String renderString = nameStack.getDisplayName();
                                if (button.getSlotPosMapIndex() == i) {
                                    info.add(renderString);
                                }
                            }
                        }
                    }


                    displayTooltips(info, xAxis, yAxis);
                }
                break;
            }
        }
        if (autoEjectButton.isMouseOver()) {
            displayTooltip(LangUtils.localize("gui.autoEject"), xAxis, yAxis);
        }
        if (clearButton.isMouseOver()) {
            displayTooltip(LangUtils.localize("gui.clear"), xAxis, yAxis);
        }
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(int xAxis, int yAxis) {
        for (RelativeSide side : RelativeSide.SIDES) {
            EnumFacing globalSide = side.getDirection(tileEntity.facing);
            Coord4D coord = Coord4D.get(tileEntity).offset(globalSide);
            IBlockState blockstate = tileEntity.getWorld().getBlockState(coord.getPos());
            Block block = blockstate.getBlock();
            int metadata = block.getMetaFromState(blockstate);
            blockList.add(Pair.of(metadata, block));
            for (int i = 0; i < slotPosMap.size(); i++) {
                int layer = currentLayer + (i);
                GuiPos guiPos = slotPosMap.get(i);
                if (0 <= layer && layer < blockList.size()) {
                    Pair<Integer, Block> integerBlockPair = blockList.get(layer);
                    ItemStack stack = new ItemStack(integerBlockPair.getRight(), 1, integerBlockPair.getLeft());
                    stack.setTagCompound(tileEntity.getTileData());
                    stack.setTagCompound(tileEntity.getUpdateTag());
                    MekanismRenderer.resetColor();
                    ClientUtil.renderItem(stack, guiLeft + guiPos.xPos + 3, guiTop + guiPos.yPos + 3);
                    MekanismRenderer.resetColor();
                }
            }
        }
        super.drawGuiContainerBackgroundLayer(xAxis, yAxis);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        TileEntity tile = (TileEntity) configurable;
        if (tile == null || mc.world.getTileEntity(tile.getPos()) == null) {
            mc.displayGuiScreen(null);
            blockList.clear();
        }
        updateEnabledButtons();
    }

    private void updateEnabledButtons() {
        autoEjectButton.enabled = configurable.getConfig().canEject(currentType);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);
        if (button == 1) {
            //Handle right clicking the side data buttons
            for (GuiSideDataButton sideDataButton : sideDataButtons) {
                if (sideDataButton.isMouseOver()) {
                    SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
                    Mekanism.packetHandler.sendToServer(new ConfigurationUpdateMessage(ConfigurationPacket.SIDE_DATA, Coord4D.get((TileEntity) configurable), 1, sideDataButton.getSlotPosMapIndex(), currentType));
                    break;
                }
            }
        }
    }

    public static class GuiPos {

        public final int xPos;
        public final int yPos;
        public final RelativeSide FacingName;

        public GuiPos(int x, int y, RelativeSide name) {
            xPos = x;
            yPos = y;
            FacingName = name;
        }
    }


}
