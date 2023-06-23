package mekanism.common.integration.groovyscript;

import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import mekanism.common.Mekanism;
import mekanism.common.integration.MekanismHooks;
import mekanism.common.integration.groovyscript.machinerecipe.*;
import mekanism.generators.common.MekanismGenerators;
import net.minecraftforge.fml.common.Loader;


public class GrSMekanismAdd {

    public static ModSupport.Container<Container> modSupportContainer;
    private static boolean loaded = false;

    private GrSMekanismAdd() {
    }


    public static void init() {
        loaded = Loader.isModLoaded(MekanismHooks.GROOVYSCRIPT_MOD_ID);
        if (!loaded) return;
        modSupportContainer = new ModSupport.Container<>(Mekanism.MODID, Mekanism.MOD_NAME, Container::new, "mek","mekceu","MekanismCEUnofficial");
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static Container getInstance() {
        return modSupportContainer.get();
    }


    public static class Container extends com.cleanroommc.groovyscript.compat.mods.mekanism.Mekanism {

        public Smelter smelter = new Smelter();

        public Container() {
            addRegistry(smelter);
        }
    }
}
