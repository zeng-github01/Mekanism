package mekanism.grs.common;

import io.netty.buffer.ByteBuf;
import mekanism.common.Version;
import mekanism.common.base.IModule;
import mekanism.common.config.MekanismConfig;
import net.minecraftforge.fml.common.Mod;

@Mod(modid = MekanismGrS.MODID,useMetadata = true)
public class MekanismGrS implements IModule {
    public static final String MODID = "mekanismgrs";

    public static Version versionNumber = new Version(999, 999, 999);

    @Override
    public Version getVersion() {
        return versionNumber;
    }

    @Override
    public String getName() {
        return "GroovyScript";
    }

    @Override
    public void writeConfig(ByteBuf dataStream, MekanismConfig config) {

    }

    @Override
    public void readConfig(ByteBuf dataStream, MekanismConfig destConfig) {

    }


    @Override
    public void resetClient() {

    }

}
