package mekanism.client.newgui.element.window;

import mekanism.client.ClientTickHandler;
import mekanism.client.gui.GuiTextColorField;
import mekanism.client.gui.element.GuiUtils;
import mekanism.client.newgui.GuiModuleTweaker;
import mekanism.client.newgui.IGuiWrapper;
import mekanism.client.newgui.element.GuiElement;
import mekanism.client.newgui.element.GuiElementHolder;
import mekanism.client.newgui.element.GuiInnerScreen;
import mekanism.client.newgui.element.button.MekanismButton;
import mekanism.client.newgui.element.custom.GuiModuleScreen;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.MekanismLang;
import mekanism.common.lib.Color;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.common.util.text.TextUtils;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.annotation.Nullable;

public class GuiColorWindow extends GuiWindow {

    public static final ResourceLocation TRANSPARENCY_GRID = MekanismUtils.getResource(ResourceType.GUI, "transparency_grid.png");
    private static final ResourceLocation HUE_PICKER = MekanismUtils.getResource(ResourceType.GUI, "color_picker.png");
    private static final int S_TILES = 10;
    private static final int V_TILES = 10;

    private final GuiModuleScreen.SelectedColorConfig selection;
    private final GuiTextColorField textField;
    @Nullable
    private final Runnable previewUpdater;
    @Nullable
    private final GuiModuleTweaker.ArmorPreview armorPreview;
    private final boolean handlesAlpha;
    private final int initialColor;

    private float hue;
    private float saturation = 0.5F;
    private float value = 0.5F;
    private float alpha = 1;
    private boolean confirmed;

    public static int getWindowWidth(boolean handlesAlpha, boolean hasPreview) {
        return (handlesAlpha ? 184 : 158) + (hasPreview ? 83 : 0);
    }

    public static int getWindowHeight(boolean handlesAlpha) {
        return handlesAlpha ? 152 : 140;
    }

    public GuiColorWindow(IGuiWrapper gui, int x, int y, GuiModuleScreen.SelectedColorConfig selection, @Nullable Runnable previewUpdater,
          @Nullable GuiModuleTweaker.ArmorPreview armorPreview) {
        super(gui, x, y, getWindowWidth(selection.handlesAlpha(), armorPreview != null), getWindowHeight(selection.handlesAlpha()),
              mekanism.common.inventory.container.SelectedWindowData.WindowType.COLOR);
        interactionStrategy = InteractionStrategy.NONE;
        this.selection = selection;
        this.previewUpdater = previewUpdater;
        this.armorPreview = armorPreview;
        handlesAlpha = selection.handlesAlpha();
        initialColor = selection.getColor();

        int extraWidth = handlesAlpha ? 26 : 0;
        int extraShadeWidth = handlesAlpha ? 20 : 0;
        int extraViewWidth = extraWidth - extraShadeWidth;
        int textOffset = handlesAlpha ? 6 : 0;

        addChild(new GuiElementHolder(gui, relativeX + 6, relativeY + 17, 41 + extraViewWidth, 82));
        addChild(new GuiColorView(gui, relativeX + 7, relativeY + 18, 39 + extraViewWidth, 80));

        addChild(new GuiElementHolder(gui, relativeX + 50 + extraViewWidth, relativeY + 17, 102 + extraShadeWidth, 82));
        addChild(new GuiShadePicker(gui, relativeX + 51 + extraViewWidth, relativeY + 18, 100 + extraShadeWidth, 80));

        addChild(new GuiElementHolder(gui, relativeX + 6, relativeY + 103, 146 + extraWidth, 10));
        addChild(new GuiHuePicker(gui, relativeX + 7, relativeY + 104, 144 + extraWidth, 8));

        if (handlesAlpha) {
            addChild(new GuiElementHolder(gui, relativeX + 6, relativeY + 115, 146 + extraWidth, 10));
            addChild(new GuiAlphaPicker(gui, relativeX + 7, relativeY + 116, 144 + extraWidth, 8));
        }

        addChild(new GuiElementHolder(gui, relativeX + 29 + textOffset, relativeY + height - 21, 65 + extraWidth - textOffset, 14));
        addChild(new MekanismButton(gui, relativeX + 98 + extraWidth, relativeY + height - 21, 54, 14, MekanismLang.BUTTON_CONFIRM.translate(),
              this::confirmSelection, null));

        if (armorPreview != null) {
            addChild(new GuiInnerScreen(gui, relativeX + 155 + extraWidth, relativeY + 17, 80, height - 24));
            addChild(new GuiArmorPreview(gui, relativeX + 155 + extraWidth, relativeY + 17, 80, height - 24, 5));
        }

        textField = new GuiTextColorField(0, getFont(), 0, 0, 63 + extraWidth - textOffset, 12);
        textField.setEnableBackgroundDrawing(false);
        textField.setCanLoseFocus(true);
        textField.setMaxStringLength(handlesAlpha ? 15 : 11);
        textField.setTextColor(screenTextColor());
        textField.setValidator(this::isTextAllowed);

        setFromColor(Color.argb(initialColor));
        updateTextFromColor();
        updateTextFieldPosition();
    }

    @Override
    public void tick() {
        super.tick();
        textField.updateCursorCounter();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean handled = super.mouseClicked(mouseX, mouseY, button);
        updateTextFieldPosition();
        if (button == 0) {
            syncTextFromColor();
        }
        boolean textHandled = button == 0 && textField.mouseClicked((int) mouseX - getGuiLeft(), (int) mouseY - getGuiTop(), button);
        return handled || textHandled;
    }

    @Override
    public boolean charTyped(char c, int keyCode) {
        if (textField.isFocused() && textField.textboxKeyTyped(c, keyCode)) {
            updateColorFromText();
            return true;
        }
        return super.charTyped(c, keyCode);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == Keyboard.KEY_RETURN) {
            confirmSelection();
            return true;
        }
        if (textField.isFocused() && textField.textboxKeyTyped('\0', keyCode)) {
            updateColorFromText();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        if (!confirmed && selection.getColor() != initialColor) {
            selection.setColor(initialColor);
            if (previewUpdater != null) {
                previewUpdater.run();
            }
        }
        super.close();
    }

    @Override
    public void renderForeground(int mouseX, int mouseY) {
        super.renderForeground(mouseX, mouseY);
        drawTitleText(MekanismLang.COLOR_PICKER.translate(), 6);
        drawTextExact(handlesAlpha ? MekanismLang.RGBA.translate() : MekanismLang.RGB.translate(), relativeX + 7, relativeY + height - 18, titleTextColor());
        updateTextFieldPosition();
        if (textField.isFocused()) {
            MekanismRenderer.resetColor();
            textField.drawTextBox();
            MekanismRenderer.resetColor();
        } else {
            syncTextFromColor();
            getFont().drawStringWithShadow(getCurrentColorText(), textField.xlocation + 1, textField.ylocation + 2, screenTextColor());
        }
    }

    private boolean isTextAllowed(String text) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (!Character.isDigit(c) && c != ',' && c != ' ') {
                return false;
            }
        }
        return true;
    }

    private void confirmSelection() {
        confirmed = true;
        if (selection.getColor() != initialColor) {
            selection.save();
        }
        close();
    }

    private Color getColor() {
        Color color = Color.hsv(hue, saturation, value);
        return handlesAlpha ? color.alpha(alpha) : color;
    }

    private void updateTextFieldPosition() {
        int textOffset = handlesAlpha ? 6 : 0;
        textField.xlocation = relativeX + 30 + textOffset;
        textField.ylocation = relativeY + height - 20;
    }

    private void updateTextFromColor() {
        textField.setText(getCurrentColorText());
    }

    private void syncTextFromColor() {
        String text = getCurrentColorText();
        if (!text.equals(textField.getText())) {
            textField.setText(text);
        }
    }

    private String getCurrentColorText() {
        Color color = getColor();
        String text = color.r() + "," + color.g() + "," + color.b();
        if (handlesAlpha) {
            text += "," + Math.round(alpha * 255);
        }
        return text;
    }

    private void updateColorFromText() {
        String[] split = textField.getText().replace(" ", "").split(",");
        if (split.length != (handlesAlpha ? 4 : 3)) {
            return;
        }
        try {
            int r = Integer.parseInt(split[0]);
            int g = Integer.parseInt(split[1]);
            int b = Integer.parseInt(split[2]);
            int a = handlesAlpha ? Integer.parseInt(split[3]) : 255;
            if (byteCheck(r) && byteCheck(g) && byteCheck(b) && byteCheck(a)) {
                setFromColor(Color.rgbai(r, g, b, a));
                updateSelectionColor();
            }
        } catch (NumberFormatException ignored) {
        }
    }

    private boolean byteCheck(int value) {
        return value >= 0 && value <= 255;
    }

    private void setFromColor(Color color) {
        double[] hsv = color.hsvArray();
        hue = (float) hsv[0];
        saturation = (float) hsv[1];
        value = (float) hsv[2];
        alpha = handlesAlpha ? color.af() : 1;
    }

    private void updateSelectionColor() {
        selection.setColor(getColor().argb());
        if (previewUpdater != null) {
            previewUpdater.run();
        }
    }

    private void drawTiledGradient(int x, int y, int width, int height) {
        int tileWidth = Math.round((float) width / S_TILES);
        int tileHeight = Math.round((float) height / V_TILES);
        for (int i = 0; i < V_TILES; i++) {
            float minV = (float) i / V_TILES;
            float maxV = (float) (i + 1) / V_TILES;
            for (int j = 0; j < S_TILES; j++) {
                float minS = (float) j / S_TILES;
                float maxS = (float) (j + 1) / S_TILES;
                Color topLeft = Color.hsv(hue, minS, maxV);
                Color topRight = Color.hsv(hue, maxS, maxV);
                Color bottomLeft = Color.hsv(hue, minS, minV);
                Color bottomRight = Color.hsv(hue, maxS, minV);
                drawGradient(x + j * tileWidth, y + (V_TILES - i - 1) * tileHeight, tileWidth, tileHeight, topLeft, topRight, bottomLeft, bottomRight);
            }
        }
    }

    private void drawGradient(int x, int y, int width, int height, Color topLeft, Color topRight, Color bottomLeft, Color bottomRight) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
              GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(x, y + height, 0).color(bottomLeft.r(), bottomLeft.g(), bottomLeft.b(), bottomLeft.a()).endVertex();
        buffer.pos(x + width, y + height, 0).color(bottomRight.r(), bottomRight.g(), bottomRight.b(), bottomRight.a()).endVertex();
        buffer.pos(x + width, y, 0).color(topRight.r(), topRight.g(), topRight.b(), topRight.a()).endVertex();
        buffer.pos(x, y, 0).color(topLeft.r(), topLeft.g(), topLeft.b(), topLeft.a()).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    private void drawColorBar(int x, int y, int width, int height) {
        for (int i = 0; i < width; i++) {
            GuiUtils.fill(x + i, y, x + i + 1, y + height, Color.hsv(((float) i / width) * 360F, 1, 1).argb());
        }
    }

    private void drawAlphaBar(int x, int y, int width, int height) {
        Color hsv = Color.hsv(hue, saturation, value);
        for (int i = 0; i < width; i++) {
            GuiUtils.fill(x + i, y, x + i + 1, y + height, hsv.alpha((float) i / width).argb());
        }
    }

    private void drawTransparencyGrid(int x, int y, int width, int height) {
        MekanismRenderer.resetColor();
        minecraft.renderEngine.bindTexture(TRANSPARENCY_GRID);
        GuiUtils.blit(x, y, width, height, 0, 0, 16, 16, 16, 16);
        MekanismRenderer.resetColor();
    }

    private void drawOutline(int x, int y, int width, int height, int color) {
        GuiUtils.fill(x, y, x + width, y + 1, color);
        GuiUtils.fill(x, y + height - 1, x + width, y + height, color);
        if (height > 2) {
            GuiUtils.fill(x, y + 1, x + 1, y + height - 1, color);
            GuiUtils.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
        }
    }

    private class GuiColorView extends GuiElement {

        private GuiColorView(IGuiWrapper gui, int x, int y, int width, int height) {
            super(gui, x, y, width, height);
        }

        @Override
        public void drawBackground(int mouseX, int mouseY, float partialTicks) {
            super.drawBackground(mouseX, mouseY, partialTicks);
            if (handlesAlpha) {
                drawTransparencyGrid(x, y, width, height);
            }
            MekanismRenderer.resetColor();
            GuiUtils.fill(x, y, x + width, y + height, getColor().argb());
            MekanismRenderer.resetColor();
        }

        @Override
        public void renderToolTip(int mouseX, int mouseY) {
            super.renderToolTip(mouseX, mouseY);
            String hex = handlesAlpha ? TextUtils.hex(false, 4, getColor().argb()) : TextUtils.hex(false, 3, getColor().rgb());
            displayTooltip(MekanismLang.GENERIC_HEX.translate(hex), mouseX, mouseY, getGuiWidth());
        }
    }

    private abstract class GuiPicker extends GuiElement {

        private boolean dragging;

        private GuiPicker(IGuiWrapper gui, int x, int y, int width, int height) {
            super(gui, x, y, width, height);
        }

        protected abstract void set(double mouseX, double mouseY);

        @Override
        public void onClick(double mouseX, double mouseY) {
            super.onClick(mouseX, mouseY);
            textField.setFocused(false);
            dragging = true;
            set(mouseX, mouseY);
        }

        @Override
        public void onDrag(double mouseX, double mouseY, double mouseXOld, double mouseYOld) {
            super.onDrag(mouseX, mouseY, mouseXOld, mouseYOld);
            if (dragging) {
                set(mouseX, mouseY);
            }
        }

        @Override
        public void onRelease(double mouseX, double mouseY) {
            super.onRelease(mouseX, mouseY);
            dragging = false;
        }
    }

    private class GuiShadePicker extends GuiPicker {

        private GuiShadePicker(IGuiWrapper gui, int x, int y, int width, int height) {
            super(gui, x, y, width, height);
        }

        @Override
        public void drawBackground(int mouseX, int mouseY, float partialTicks) {
            super.drawBackground(mouseX, mouseY, partialTicks);
            drawTiledGradient(x, y, width, height);
            int posX = x + Math.round(saturation * width) - 2;
            int posY = y + Math.round((1 - value) * height) - 2;
            drawOutline(posX, posY, 5, 5, 0xFFFFFFFF);
            GuiUtils.fill(posX + 1, posY + 1, posX + 4, posY + 4, getColor().alpha(1).argb());
        }

        @Override
        protected void set(double mouseX, double mouseY) {
            saturation = MathHelper.clamp((float) (mouseX - x) / width, 0, 1);
            value = 1 - MathHelper.clamp((float) (mouseY - y) / height, 0, 1);
            updateTextFromColor();
            updateSelectionColor();
        }
    }

    private class GuiHuePicker extends GuiPicker {

        private GuiHuePicker(IGuiWrapper gui, int x, int y, int width, int height) {
            super(gui, x, y, width, height);
        }

        @Override
        public void drawBackground(int mouseX, int mouseY, float partialTicks) {
            super.drawBackground(mouseX, mouseY, partialTicks);
            drawColorBar(x, y, width, height);
            int posX = Math.round((hue / 360F) * (width - 3));
            MekanismRenderer.resetColor();
            minecraft.renderEngine.bindTexture(HUE_PICKER);
            GuiUtils.blit(x - 2 + posX, y - 2, 0, 0, 7, 12, 12, 12);
            GuiUtils.fill(x + posX, y, x + posX + 3, y + height, Color.hsv(hue, 1, 1).argb());
            MekanismRenderer.resetColor();
        }

        @Override
        protected void set(double mouseX, double mouseY) {
            hue = MathHelper.clamp((float) (mouseX - x) / width, 0, 1) * 360F;
            updateTextFromColor();
            updateSelectionColor();
        }
    }

    private class GuiAlphaPicker extends GuiPicker {

        private GuiAlphaPicker(IGuiWrapper gui, int x, int y, int width, int height) {
            super(gui, x, y, width, height);
        }

        @Override
        public void drawBackground(int mouseX, int mouseY, float partialTicks) {
            super.drawBackground(mouseX, mouseY, partialTicks);
            drawTransparencyGrid(x, y, width, height);
            drawAlphaBar(x, y, width, height);
            int posX = Math.round(GuiColorWindow.this.alpha * (width - 3));
            MekanismRenderer.resetColor();
            minecraft.renderEngine.bindTexture(HUE_PICKER);
            GuiUtils.blit(x - 2 + posX, y - 2, 0, 0, 7, 12, 12, 12);
            drawTransparencyGrid(x + posX, y, 3, height);
            GuiUtils.fill(x + posX, y, x + posX + 3, y + height, getColor().argb());
            MekanismRenderer.resetColor();
        }

        @Override
        protected void set(double mouseX, double mouseY) {
            GuiColorWindow.this.alpha = MathHelper.clamp((float) (mouseX - x) / width, 0, 1);
            updateTextFromColor();
            updateSelectionColor();
        }
    }

    private class GuiArmorPreview extends GuiElement {

        private final int border;
        private boolean dragging;
        private double lastMouseX;
        private float rotation;

        private GuiArmorPreview(IGuiWrapper gui, int x, int y, int width, int height, int border) {
            super(gui, x, y, width, height);
            this.border = border;
        }

        @Override
        public void renderForeground(int mouseX, int mouseY) {
            super.renderForeground(mouseX, mouseY);
            if (armorPreview == null) {
                return;
            }
            boolean depthEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
            boolean cullEnabled = GL11.glIsEnabled(GL11.GL_CULL_FACE);
            boolean blendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
            boolean lightingEnabled = GL11.glIsEnabled(GL11.GL_LIGHTING);
            boolean colorMaterialEnabled = GL11.glIsEnabled(GL11.GL_COLOR_MATERIAL);
            boolean rescaleNormalEnabled = GL11.glIsEnabled(GL12.GL_RESCALE_NORMAL);

            GlStateManager.pushMatrix();
            GlStateManager.enableDepth();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableRescaleNormal();
            GlStateManager.disableCull();
            MekanismRenderer.resetColor();
            int scale = Math.max(16, (Math.min(width, height) - 2 * border) / 2);
            renderEntity(relativeX + width / 2, relativeY + height - 2 - border - Math.max(0, (height - width) / 2), scale, rotation, armorPreview.get());
            MekanismRenderer.resetColor();
            GlStateManager.disableLighting();
            GlStateManager.popMatrix();

            if (colorMaterialEnabled) {
                GlStateManager.enableColorMaterial();
            } else {
                GlStateManager.disableColorMaterial();
            }
            if (rescaleNormalEnabled) {
                GlStateManager.enableRescaleNormal();
            } else {
                GlStateManager.disableRescaleNormal();
            }
            if (lightingEnabled) {
                GlStateManager.enableLighting();
            } else {
                GlStateManager.disableLighting();
            }
            if (cullEnabled) {
                GlStateManager.enableCull();
            } else {
                GlStateManager.disableCull();
            }
            if (blendEnabled) {
                GlStateManager.enableBlend();
            } else {
                GlStateManager.disableBlend();
            }
            if (depthEnabled) {
                GlStateManager.enableDepth();
            } else {
                GlStateManager.disableDepth();
            }
            MekanismRenderer.resetColor();
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            super.onClick(mouseX, mouseY);
            textField.setFocused(false);
            dragging = true;
            lastMouseX = mouseX;
        }

        @Override
        public void onRelease(double mouseX, double mouseY) {
            super.onRelease(mouseX, mouseY);
            dragging = false;
        }

        @Override
        public void onDrag(double mouseX, double mouseY, double mouseXOld, double mouseYOld) {
            super.onDrag(mouseX, mouseY, mouseXOld, mouseYOld);
            if (dragging) {
                rotation = MathHelper.wrapDegrees(rotation - (float) ((mouseX - lastMouseX) / 2.5F));
                lastMouseX = mouseX;
            }
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
            if (isMouseOver(mouseX, mouseY)) {
                rotation = MathHelper.wrapDegrees(rotation + (float) (delta / 12D));
                return true;
            }
            return super.mouseScrolled(mouseX, mouseY, delta);
        }

        private void renderEntity(int posX, int posY, int scale, float rotation, EntityLivingBase entity) {
            GlStateManager.enableColorMaterial();
            GlStateManager.pushMatrix();
            GlStateManager.translate(posX, posY, 50.0F);
            GlStateManager.scale(-scale, scale, scale);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            float renderYawOffset = entity.renderYawOffset;
            float rotationYaw = entity.rotationYaw;
            float rotationPitch = entity.rotationPitch;
            float prevRotationYawHead = entity.prevRotationYawHead;
            float rotationYawHead = entity.rotationYawHead;
            GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
            RenderHelper.enableStandardItemLighting();
            GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
            entity.renderYawOffset = rotation;
            entity.rotationYaw = rotation;
            entity.rotationPitch = 0;
            entity.rotationYawHead = rotation;
            entity.prevRotationYawHead = rotation;
            RenderManager renderManager = minecraft.getRenderManager();
            renderManager.setPlayerViewY(180.0F);
            renderManager.setRenderShadow(false);
            try {
                if (entity instanceof EntityPlayer player) {
                    ClientTickHandler.setModelVisibility(player, renderManager.getEntityRenderObject(player), false);
                }
                renderManager.renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
            } finally {
                if (entity instanceof EntityPlayer player) {
                    ClientTickHandler.setModelVisibility(player, renderManager.getEntityRenderObject(player), true);
                }
                renderManager.setRenderShadow(true);
                entity.renderYawOffset = renderYawOffset;
                entity.rotationYaw = rotationYaw;
                entity.rotationPitch = rotationPitch;
                entity.prevRotationYawHead = prevRotationYawHead;
                entity.rotationYawHead = rotationYawHead;
                GlStateManager.popMatrix();
                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableRescaleNormal();
                GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
                GlStateManager.disableTexture2D();
                GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            }
        }
    }
}
