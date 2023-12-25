package mekanism.grs.common.integration.groovyscript;

import com.cleanroommc.groovyscript.api.GroovyPlugin;
import com.cleanroommc.groovyscript.compat.mods.GroovyContainer;
import com.cleanroommc.groovyscript.compat.mods.ModPropertyContainer;
import mekanism.common.Mekanism;
import mekanism.grs.common.MekanismGrS;
import org.jetbrains.annotations.NotNull;


public class GrSMekanismAdd implements GroovyPlugin {


    public static MekanismAdd get() {
        return new MekanismAdd();
    }


    @Override
    public @NotNull String getModId() {
        return MekanismGrS.MODID;
    }

    @Override
    public @NotNull String getModName() {
        return Mekanism.MOD_NAME + "GrS";
    }

    @Override
    public ModPropertyContainer createModPropertyContainer() {
        return get();
    }

    @Override
    public void onCompatLoaded(GroovyContainer<?> groovyContainer) {
    }
}
