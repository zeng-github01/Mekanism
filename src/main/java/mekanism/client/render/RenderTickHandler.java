package mekanism.client.render;

import mekanism.api.Coord4D;
import mekanism.api.MekanismAPI;
import mekanism.api.Pos3D;
import mekanism.api.radiation.capability.IRadiationEntity;
import mekanism.client.render.particle.EntityJetpackFlameFX;
import mekanism.client.render.particle.EntityJetpackSmokeFX;
import mekanism.client.render.particle.EntityScubaBubbleFX;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.content.gear.IBlastingItem;
import mekanism.common.content.gear.IModuleContainerItem;
import mekanism.common.item.ItemFlamethrower;
import mekanism.common.lib.radiation.RadiationManager;
import mekanism.common.util.MekanismUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.Map;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class RenderTickHandler {

    public Random rand = new Random();
    public Minecraft mc = Minecraft.getMinecraft();

    public static double prevRadiation = 0;

    @SubscribeEvent
    public void filterTooltips(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof IModuleContainerItem containerItem) {
            containerItem.filterTooltips(stack, event.getToolTip());
        }
    }

    @SubscribeEvent
    public void tickEnd(RenderTickEvent event) {
        if (event.phase == Phase.END) {
            if (mc.player != null && mc.world != null && !mc.isGamePaused() && mc.playerController != null) {
                FontRenderer font = mc.fontRenderer;
                if (font == null) {
                    return;
                }

                EntityPlayer player = mc.player;
                World world = mc.player.world;
                RayTraceResult pos = player.rayTrace(40.0D, 1.0F);
                if (pos != null) {
                    Coord4D obj = new Coord4D(pos.getBlockPos(), world);
                    Block block = obj.getBlock(world);

                    if (block != null && MekanismAPI.debug && mc.currentScreen == null
                            && !mc.gameSettings.showDebugInfo) {
                        String tileDisplay = "";

                        if (obj.getTileEntity(world) != null) {
                            if (obj.getTileEntity(world).getClass() != null) {
                                tileDisplay = obj.getTileEntity(world).getClass().getSimpleName();
                            }
                        }

                        font.drawStringWithShadow("Block: " + block.getTranslationKey(), 1, 1, 0x404040);
                        font.drawStringWithShadow("Metadata: " + obj.getBlockState(world), 1, 10, 0x404040);
                        font.drawStringWithShadow("Location: " + MekanismUtils.getCoordDisplay(obj), 1, 19, 0x404040);
                        font.drawStringWithShadow("TileEntity: " + tileDisplay, 1, 28, 0x404040);
                        font.drawStringWithShadow("Side: " + pos.sideHit, 1, 37, 0x404040);
                    }
                }

                // Traverse a copy of jetpack state and do animations
                Mekanism.playerState.getActiveJetpacks().forEach(uuid -> {
                    EntityPlayer p = mc.world.getPlayerEntityByUUID(uuid);
                    if (p != null) {
                        Pos3D playerPos = new Pos3D(p).translate(0, p.getEyeHeight(), 0);
                        Vec3d playerMotion = new Vec3d(player.motionX, player.motionY, player.motionZ);
                        float random = (world.rand.nextFloat() - 0.5F) * 0.1F;
                        //This positioning code is somewhat cursed, but it seems to be mostly working and entity pose code seems cursed in general
                        float xRot;
                        if (p.isSneaking()) {
                            xRot = 20;
                            playerPos = playerPos.translate(0, 0.125, 0);
                        } else {
                            float f = p.getSwingProgress(event.renderTickTime);
                            if (p.isElytraFlying()) {
                                float f1 = (float) p.getTicksElytraFlying() + event.renderTickTime;
                                float f2 = clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
                                xRot = f2 * (-90.0F - p.rotationPitch);
                            } else {
                                float f3 = p.isInWater() ? -90.0F - p.rotationPitch : -90.0F;
                                xRot = lerp(f, 0.0F, f3);
                            }
                            xRot = -xRot;
                            Pos3D eyeAdjustments;
                            if (p.isElytraFlying() && (p != player || mc.gameSettings.thirdPersonView != 0)) {
                                eyeAdjustments = new Pos3D(0, p.getEyeHeight(), 0).rotatePitch(xRot).rotateYaw(p.rotationYaw);
                            }  /*else if (p.getSwingProgress()) {
                                eyeAdjustments = new Pos3D(0, p.getEyeHeight(), 0).rotatePitch(xRot).rotateYaw(p.rotationYaw).translate(0, 0.5, 0);
                            } */ else {
                                eyeAdjustments = new Pos3D(0, p.getEyeHeight(), 0).rotatePitch(xRot).rotateYaw(p.rotationYaw);
                            }
                            playerPos = new Pos3D(p.posX + eyeAdjustments.x, p.posY + eyeAdjustments.y, p.posZ + eyeAdjustments.z);
                        }
                        Pos3D vLeft = new Pos3D(-0.43, -0.55, -0.54).rotatePitch(xRot).rotateYaw(p.rotationYaw);
                        renderJetpackSmoke(world, playerPos.translate(vLeft).translate(playerMotion), vLeft.scale(0.2).translate(playerMotion).translate(vLeft.scale(random)));
                        Pos3D vRight = new Pos3D(0.43, -0.55, -0.54).rotatePitch(xRot).rotateYaw(p.rotationYaw);
                        renderJetpackSmoke(world, playerPos.translate(vRight).translate(playerMotion), vRight.scale(0.2).translate(playerMotion).translate(vRight.scale(random)));
                        Pos3D vCenter = new Pos3D((world.rand.nextFloat() - 0.5) * 0.4, -0.86, -0.30).rotatePitch(xRot).rotateYaw(p.rotationYaw);
                        renderJetpackSmoke(world, playerPos.translate(vCenter).translate(playerMotion), vCenter.scale(0.2).translate(playerMotion));
                    }
                });

                // Traverse a copy of gasmask state and do animations
                if (world.getTotalWorldTime() % 4 == 0) {
                    Mekanism.playerState.getActiveScubaMask().forEach(uuid -> {
                        EntityPlayer p = mc.world.getPlayerEntityByUUID(uuid);
                        if (p != null && p.isInWater()) {
                            Pos3D vec = new Pos3D(0.4, 0.4, 0.4).multiply(p.getLook(1)).translate(0, -0.2, 0);
                            Pos3D motion = vec.scale(0.2).translate(new Pos3D(p.motionX, p.motionY, p.motionZ));
                            Pos3D v = new Pos3D(p).translate(0, p.getEyeHeight(), 0).translate(vec);
                            spawnAndSetParticle(EnumParticleTypes.WATER_BUBBLE, world, v.x, v.y, v.z, motion.x, motion.y + 0.2, motion.z);
                        }
                    });

                    world.playerEntities.forEach(p -> {
                        if (!Mekanism.playerState.isFlamethrowerOn(p) && !p.isSwingInProgress) {
                            ItemStack currentItem = p.getHeldItemMainhand();
                            if (!currentItem.isEmpty() && currentItem.getItem() instanceof ItemFlamethrower flamethrower && flamethrower.getGas(currentItem) != null) {
                                Pos3D flameVec;
                                boolean rightHanded = p.getPrimaryHand() == EnumHandSide.RIGHT;
                                if (player == p && mc.gameSettings.thirdPersonView == 0) {
                                    flameVec = new Pos3D(1, 1, 1)
                                            .multiply(p.getLook(event.renderTickTime))
                                            .rotateYaw(rightHanded ? 15 : -15)
                                            .translate(0, p.getEyeHeight() - 0.1, 0);
                                } else {
                                    double flameXCoord = rightHanded ? -0.2 : 0.2;
                                    double flameYCoord = 1;
                                    double flameZCoord = 1.2;
                                    if (p.isSneaking()) {
                                        flameYCoord -= 0.65;
                                        flameZCoord -= 0.15;
                                    }
                                    flameVec = new Pos3D(flameXCoord, flameYCoord, flameZCoord).rotateYaw(p.rotationYaw);
                                }
                                Vec3d motion = new Vec3d(p.motionX, p.motionY, p.motionZ);
                                Vec3d flameMotion = new Vec3d(motion.x, p.onGround ? 0 : motion.y, motion.z);
                                Vec3d mergedVec = new Vec3d(p.posX, p.posY, p.posZ).add(flameVec);
                                spawnAndSetParticle(EnumParticleTypes.FLAME, world, mergedVec.x, mergedVec.y, mergedVec.z, flameMotion.x, flameMotion.y, flameMotion.z);
                            }
                        }
                    });
                }

                if (MekanismUtils.isPlayingMode(player)) {
                    if (player.hasCapability(Capabilities.RADIATION_ENTITY_CAPABILITY, null)) {
                        IRadiationEntity c = player.getCapability(Capabilities.RADIATION_ENTITY_CAPABILITY, null);
                        double radiation = c.getRadiation();
                        double severity = RadiationManager.RadiationScale.getScaledDoseSeverity(radiation) * 0.8;
                        if (prevRadiation < severity) {
                            prevRadiation = Math.min(severity, prevRadiation + 0.01);
                        }
                        if (prevRadiation > severity) {
                            prevRadiation = Math.max(severity, prevRadiation - 0.01);
                        }
                        if (severity > RadiationManager.BASELINE) {
                            int effect = (int) (prevRadiation * 255);
                            int color = (0x701E1E << 8) + effect;
                            MekanismRenderer.renderColorOverlay(0, 0, mc.displayWidth, mc.displayHeight, color);
                        }
                    }
                }
            }
        }
    }


    private void renderJetpackSmoke(World world, Vec3d pos, Vec3d motion) {
        spawnAndSetParticle(EnumParticleTypes.FLAME, world, pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
        spawnAndSetParticle(EnumParticleTypes.SMOKE_NORMAL, world, pos.x, pos.y, pos.z, motion.x, motion.y, motion.z);
    }


    public static float clamp(float a, float min, float max) {
        return a < min ? min : (Math.min(a, max));
    }

    public static float lerp(float pDelta, float pStart, float pEnd) {
        return pStart + pDelta * (pEnd - pStart);
    }

    public void spawnAndSetParticle(EnumParticleTypes s, World world, double x, double y, double z, double velX, double velY, double velZ) {
        Particle fx = null;
        if (s.equals(EnumParticleTypes.FLAME)) {
            fx = new EntityJetpackFlameFX(world, x, y, z, velX, velY, velZ);
        } else if (s.equals(EnumParticleTypes.SMOKE_NORMAL)) {
            fx = new EntityJetpackSmokeFX(world, x, y, z, velX, velY, velZ);
        } else if (s.equals(EnumParticleTypes.WATER_BUBBLE)) {
            fx = new EntityScubaBubbleFX(world, x, y, z, velX, velY, velZ);
        }
        mc.effectRenderer.addEffect(fx);
    }


    @SubscribeEvent(priority = EventPriority.HIGH)
    public void renderExtraBlockBreak(DrawBlockHighlightEvent event) {
        EntityPlayer player = event.getPlayer();
        if (player == null) {
            return;
        }
        ItemStack stack = player.getHeldItemMainhand();
        RayTraceResult rayTraceResult = event.getTarget();
        if (!stack.isEmpty() && stack.getItem() instanceof IBlastingItem tool) {
            event.setCanceled(true);
            if (event.getSubID() == 0 && rayTraceResult.typeOfHit.equals(RayTraceResult.Type.BLOCK)) {
                World world = player.getEntityWorld();
                BlockPos pos = rayTraceResult.getBlockPos();
                IBlockState blockState = world.getBlockState(pos);
                Map<BlockPos, IBlockState> blocks = tool.getBlastedBlocks(world, player, stack, pos, blockState);
                if (!blocks.isEmpty()) {
                    blocks.forEach((key, value) -> drawSelectionBox(player, rayTraceResult, key, value, event.getSubID(), event.getPartialTicks()));
                } else {
                    drawSelectionBox(player, rayTraceResult, pos, blockState, event.getSubID(), event.getPartialTicks());
                }
            }
        }
    }


    public void drawSelectionBox(EntityPlayer player, RayTraceResult movingObjectPositionIn, BlockPos blockpos, IBlockState iblockstate, int execute, float partialTicks) {
        if (execute == 0 && movingObjectPositionIn.typeOfHit == RayTraceResult.Type.BLOCK) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.glLineWidth(2.0F);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            if (iblockstate.getMaterial() != Material.AIR && player.world.getWorldBorder().contains(blockpos)) {
                double d3 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
                double d4 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
                double d5 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;
                float millis = (float) (System.currentTimeMillis() % 10000L) / 10000.0F;
                Color color = Color.getHSBColor(millis, 0.8F, 0.8F);
                float red = (float) color.getRed() / 255.0F;
                float green = (float) color.getGreen() / 255.0F;
                float blue = (float) color.getBlue() / 255.0F;
                drawSelectionBoundingBox(iblockstate.getSelectedBoundingBox(player.world, blockpos).grow(0.0020000000949949026D).offset(-d3, -d4, -d5), red, green, blue, 0.390625F); //draw Blinking Block
                RenderGlobal.drawSelectionBoundingBox(iblockstate.getSelectedBoundingBox(player.world, blockpos).grow(0.0020000000949949026D).offset(-d3, -d4, -d5), red, green, blue, 0.4F); //draw Outlined Bounding Box
            }
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }
    }

    private void drawSelectionBoundingBox(AxisAlignedBB box, float red, float green, float blue, float alpha) {
        drawBoundingBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha);
    }

    public static void drawBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        alpha *= (float) Math.abs(Math.sin((double) Minecraft.getSystemTime() / 100.0 * 0.3D));
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(5, DefaultVertexFormats.POSITION_COLOR);
        drawBoundingBox(bufferbuilder, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);
        tessellator.draw();
    }

    public static void drawBoundingBox(BufferBuilder buffer, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float red, float green, float blue, float alpha) {
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, minY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(minX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, minZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
        buffer.pos(maxX, maxY, maxZ).color(red, green, blue, alpha).endVertex();
    }

}
