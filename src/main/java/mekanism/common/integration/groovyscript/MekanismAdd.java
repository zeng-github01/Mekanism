package mekanism.common.integration.groovyscript;

import com.cleanroommc.groovyscript.compat.mods.mekanism.Mekanism;
import mekanism.common.integration.groovyscript.machinerecipe.*;
import mekanism.generators.common.MekanismGenerators;
import net.minecraftforge.fml.common.Loader;

public class MekanismAdd extends Mekanism {
    public IsotopicCentrifuge isotopicCentrifuge = new IsotopicCentrifuge();
    public NutritionalLiquifier nutritionalLiquifier = new NutritionalLiquifier();
    public OrganicFarm organicFarm = new OrganicFarm();
    public AntiprotonicNucleosynthesizer antiprotonicNucleosynthesizer = new AntiprotonicNucleosynthesizer();
    public Stamping stamping = new Stamping();
    public Rolling rolling = new Rolling();
    public Brushed brushed = new Brushed();
    public Turning turning = new Turning();
    public Alloy alloy = new Alloy();
    public CellExtractor cellExtractor = new CellExtractor();
    public CellSeparator cellSeparator = new CellSeparator();
    public Recycler recycler = new Recycler();

    public Smelter smelting = new Smelter();
    public AmbientAccumulator ambient = new AmbientAccumulator();
    public FusionCooling fusionCooling = new FusionCooling();

    public MekanismAdd() {
        addRegistry(smelting);
        addRegistry(isotopicCentrifuge);
        addRegistry(nutritionalLiquifier);
        addRegistry(organicFarm);
        addRegistry(antiprotonicNucleosynthesizer);
        addRegistry(stamping);
        addRegistry(rolling);
        addRegistry(brushed);
        addRegistry(turning);
        addRegistry(alloy);
        addRegistry(cellExtractor);
        addRegistry(cellSeparator);
        addRegistry(recycler);
        addRegistry(ambient);
        if (Loader.isModLoaded(MekanismGenerators.MODID)){
            addRegistry(fusionCooling);
        }
    }

}
