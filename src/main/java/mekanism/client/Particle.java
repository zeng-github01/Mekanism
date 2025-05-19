package mekanism.client;

import mekanism.client.render.particle.RadiationParticle;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.common.util.EnumHelper;

public class Particle {


    public static final EnumParticleTypes radiation = EnumHelper.addEnum(EnumParticleTypes.class, "radiation", new Class<?>[]{String.class, int.class, boolean.class}, "radiation", nextParticleId(), false);


    private static int nextParticleId() {
        EnumParticleTypes[] allParticles = EnumParticleTypes.values();
        int maxId = -1;
        for (EnumParticleTypes particle : allParticles) {
            if (particle.getParticleID() > maxId) {
                maxId = particle.getParticleID();
            }
        }
        return maxId + 1;
    }

    public static void registerParticles() {
        Minecraft.getMinecraft().effectRenderer.registerParticle(radiation.getParticleID(), new RadiationParticle.Factory());
    }

}
