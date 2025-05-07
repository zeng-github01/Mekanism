package mekanism.client.render.hud;

import mekanism.client.render.MekanismRenderer;
import mekanism.common.item.interfaces.IModeItem;
import mekanism.common.lib.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MekanismStatusOverlay {

    public static final MekanismStatusOverlay INSTANCE = new MekanismStatusOverlay();

    private int modeSwitchTimer = 0;
    private long lastTick;

    private MekanismStatusOverlay() {
    }

    public void setTimer() {
        modeSwitchTimer = 100;
    }

    public void render(RenderGameOverlayEvent.Post event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            if (!minecraft.gameSettings.hideGUI && modeSwitchTimer > 1 && minecraft.player != null) {
                ItemStack stack = minecraft.player.getHeldItemMainhand();
                if (IModeItem.isModeItem(stack, EntityEquipmentSlot.MAINHAND)) {
                    ITextComponent scrollTextComponent = ((IModeItem) stack.getItem()).getScrollTextComponent(stack);
                    if (scrollTextComponent != null) {
                        Color color = Color.rgbad(1, 1, 1, modeSwitchTimer / 100F);
                        FontRenderer font = minecraft.fontRenderer;
                        int componentWidth = font.getStringWidth(scrollTextComponent.getFormattedText());
                        int targetShift = Math.max(59, Math.max(GuiIngameForge.left_height, GuiIngameForge.right_height));
                        EntityPlayer player = minecraft.player;
                        if (minecraft.gameSettings != null && player != null && player.isCreative()) {
                            //Same shift as done in Gui#renderSelectedItemName
                            targetShift -= 14;
                        } else if (minecraft.ingameGUI.overlayMessageTime > 0) {
                            //If we are in survival though that means our thing will end up intersecting the subtitle text if there is any,
                            // so we need to check if there is, and if so shift our target further
                            targetShift += 14;
                        }
                        //Shift the rendering to be above the previous line
                        targetShift += 13;
                        GlStateManager.pushMatrix();
                        GlStateManager.translate((event.getResolution().getScaledWidth() - componentWidth) / 2F, event.getResolution().getScaledHeight() - targetShift, 0);
                        minecraft.fontRenderer.drawString(scrollTextComponent.getFormattedText(), 0, 0, color.argb());
                        GlStateManager.popMatrix();
                        MekanismRenderer.resetColor();
                        minecraft.renderEngine.bindTexture(Gui.ICONS);
                    }
                }

                //Only decrement the switch timer once a tick
                if (lastTick != minecraft.player.world.getTotalWorldTime()) {
                    lastTick = minecraft.player.world.getTotalWorldTime();
                    modeSwitchTimer--;
                }

            }
        }

    }
}
