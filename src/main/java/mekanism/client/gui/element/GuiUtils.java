package mekanism.client.gui.element;

import mekanism.api.gas.GasStack;
import mekanism.client.render.MekanismRenderer;
import mekanism.common.InfuseStorage;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static mekanism.client.gui.element.GuiElement.mc;


@SideOnly(Side.CLIENT)
public class GuiUtils {

    public static void drawGasBarSprite(int xPos, int yPos, int sizeX, int sizeY, int displayInt, GasStack gasStack, boolean vertical) {
        if (displayInt > 0) {
            if (gasStack != null) {
                mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                MekanismRenderer.color(gasStack);
                if (vertical) {
                    drawTiledSprite(xPos + 1, yPos + 1, sizeY - 2, sizeX - 2, displayInt, gasStack.getGas().getSprite(), TilingDirection.DOWN_RIGHT);
                } else {
                    drawTiledSprite(xPos + 1, yPos + 1, sizeY - 2, displayInt, sizeY - 2, gasStack.getGas().getSprite(), TilingDirection.DOWN_RIGHT);
                }
                MekanismRenderer.resetColor();
            }
        }
    }

    public static void drawFluidBarSprite(int xPos, int yPos, int sizeX, int sizeY, int displayInt, FluidStack fluidStack, boolean vertical) {
        if (displayInt > 0) {
            if (fluidStack != null) {
                mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                MekanismRenderer.color(fluidStack);
                if (vertical) {
                    drawTiledSprite(xPos + 1, yPos + 1, sizeY - 2, sizeX - 2, displayInt, MekanismRenderer.getFluidTexture(fluidStack, MekanismRenderer.FluidType.STILL), TilingDirection.DOWN_RIGHT);
                } else {
                    drawTiledSprite(xPos + 1, yPos + 1, sizeY - 2, displayInt, sizeY - 2, MekanismRenderer.getFluidTexture(fluidStack, MekanismRenderer.FluidType.STILL), TilingDirection.DOWN_RIGHT);
                }
                MekanismRenderer.resetColor();
            }
        }
    }


    public static void drawInfuseBarSprite(int xPos, int yPos, int sizeX, int sizeY, int displayInt, InfuseStorage infuseStorage, boolean vertical) {
        if (displayInt > 0) {
            if (infuseStorage.getType() != null) {
                mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                if (vertical) {
                    drawTiledSprite(xPos + 1, yPos + 1, sizeY - 2, sizeX - 2, displayInt, infuseStorage.getType().sprite, TilingDirection.DOWN_RIGHT);
                } else {
                    drawTiledSprite(xPos + 1, yPos + 1, sizeY - 2, displayInt, sizeY - 2, infuseStorage.getType().sprite, TilingDirection.DOWN_RIGHT);
                }
                MekanismRenderer.resetColor();
            }
        }
    }


    public static void drawTiledSprite(int xPosition, int yPosition, int yOffset, int desiredWidth, int desiredHeight, TextureAtlasSprite sprite, TilingDirection tilingDirection) {
        drawTiledSprite(xPosition, yPosition, yOffset, desiredWidth, desiredHeight, sprite, 16, 16, 0, tilingDirection);
    }

    public static void drawTiledSprite(int xPosition, int yPosition, int yOffset, int desiredWidth, int desiredHeight, TextureAtlasSprite sprite, int textureWidth, int textureHeight, int zLevel, TilingDirection tilingDirection) {
        drawTiledSprite(xPosition, yPosition, yOffset, desiredWidth, desiredHeight, sprite, textureWidth, textureHeight, zLevel, tilingDirection, true);
    }

    public static void drawTiledSprite(int xPosition, int yPosition, int yOffset, int desiredWidth, int desiredHeight, TextureAtlasSprite sprite, int textureWidth, int textureHeight, int zLevel, TilingDirection tilingDirection, boolean blend) {
        if (desiredWidth == 0 || desiredHeight == 0 || textureWidth == 0 || textureHeight == 0) {
            return;
        }

        int xTileCount = desiredWidth / textureWidth;
        int xRemainder = desiredWidth - (xTileCount * textureWidth);
        int yTileCount = desiredHeight / textureHeight;
        int yRemainder = desiredHeight - (yTileCount * textureHeight);
        int yStart = yPosition + yOffset;
        float uMin = sprite.getMinU();
        float uMax = sprite.getMaxU();
        float vMin = sprite.getMinV();
        float vMax = sprite.getMaxV();
        float uDif = uMax - uMin;
        float vDif = vMax - vMin;

        //Note: We still use the tesselator as that is what GuiGraphics#innerBlit does
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

        for (int xTile = 0; xTile <= xTileCount; xTile++) {
            int width = (xTile == xTileCount) ? xRemainder : textureWidth;
            if (width == 0) {
                break;
            }
            int x = xPosition + (xTile * textureWidth);
            int maskRight = textureWidth - width;
            int shiftedX = x + textureWidth - maskRight;
            float uLocalDif = uDif * maskRight / textureWidth;
            float uLocalMin;
            float uLocalMax;
            if (tilingDirection.right) {
                uLocalMin = uMin;
                uLocalMax = uMax - uLocalDif;
            } else {
                uLocalMin = uMin + uLocalDif;
                uLocalMax = uMax;
            }
            for (int yTile = 0; yTile <= yTileCount; yTile++) {
                int height = (yTile == yTileCount) ? yRemainder : textureHeight;
                if (height == 0) {
                    //Note: We don't want to fully break out because our height will be zero if we are looking to
                    // draw the remainder, but there is no remainder as it divided evenly
                    break;
                }
                int y = yStart - ((yTile + 1) * textureHeight);
                int maskTop = textureHeight - height;
                float vLocalDif = vDif * maskTop / textureHeight;
                float vLocalMin;
                float vLocalMax;
                if (tilingDirection.down) {
                    vLocalMin = vMin;
                    vLocalMax = vMax - vLocalDif;
                } else {
                    vLocalMin = vMin + vLocalDif;
                    vLocalMax = vMax;
                }
                bufferbuilder.pos(x, y + textureHeight, zLevel).tex(uLocalMin, vLocalMax).endVertex();
                bufferbuilder.pos(shiftedX, y + textureHeight, zLevel).tex(uLocalMax, vLocalMax).endVertex();
                bufferbuilder.pos(shiftedX, y + maskTop, zLevel).tex(uLocalMax, vLocalMin).endVertex();
                bufferbuilder.pos(x, y + maskTop, zLevel).tex(uLocalMin, vLocalMin).endVertex();
            }
        }
        tessellator.draw();
    }

    public enum TilingDirection {
        /**
         * Textures are being tiled/filled from top left to bottom right.
         */
        DOWN_RIGHT(true, true),
        /**
         * Textures are being tiled/filled from top right to bottom left.
         */
        DOWN_LEFT(true, false),
        /**
         * Textures are being tiled/filled from bottom left to top right.
         */
        UP_RIGHT(false, true),
        /**
         * Textures are being tiled/filled from bottom right to top left.
         */
        UP_LEFT(false, false);

        private final boolean down;
        private final boolean right;

        TilingDirection(boolean down, boolean right) {
            this.down = down;
            this.right = right;
        }
    }
}
