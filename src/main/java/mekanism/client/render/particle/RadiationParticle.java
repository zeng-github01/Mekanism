package mekanism.client.render.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RadiationParticle extends ParticleSmokeNormal {

    private static Minecraft mc = FMLClientHandler.instance().getClient();

    public RadiationParticle(World world, double posX, double posY, double posZ, double velX, double velY, double velZ) {
        super(world, posX, posY, posZ, velX, velY, velZ, 1.0F);
        setRBGColorF(0.2F, 0.66F, 0.32F);
    }

    @Override
    public int getBrightnessForRender(float partialTick) {
        return 190 + (int) (20F * (1.0F - mc.gameSettings.gammaSetting));
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        if (particleAge > 0) {
            super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
        }
    }


    @SideOnly(Side.CLIENT)
    public static class Factory implements IParticleFactory {
        public Particle createParticle(int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... p_178902_15_) {
            return new RadiationParticle(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        }
    }

}
