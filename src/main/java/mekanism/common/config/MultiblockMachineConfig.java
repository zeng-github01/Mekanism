package mekanism.common.config;

import mekanism.common.config.options.BooleanOption;
import mekanism.common.config.options.DoubleOption;
import mekanism.common.config.options.IntOption;
import mekanism.multiblockmachine.common.block.states.BlockStateMultiblockMachineGenerator.MultiblockMachineGeneratorType;

public class MultiblockMachineConfig extends BaseConfig{

    public final BooleanOption destroyDisabledBlocks = new BooleanOption(this, "multiblock", "DestroyDisabledMultiBlockBlocks", true,
            "If machine is disabled in config, do we set its block to air if it is found in world?");

    public final DoubleOption largewindGeneratorStorage = new DoubleOption(this, "multiblock",
            "MultiblockWindGeneratorStorage", 58800000D , "Energy capable of being stored");

    public final DoubleOption largewindGenerationMax = new DoubleOption(this, "multiblock", "LargeWindGeneratorMax", 294000D,
            "Maximum base generation value of the Large Wind Generator.");

    public final DoubleOption largewindGenerationMin = new DoubleOption(this, "multiblock", "LargeWindGeneratorMin", 60D,
            "Minimum base generation value of the Large Wind Generator.");

    public final IntOption largewindGenerationMinY = new IntOption(this, "multiblock", "LargeWindGenerationMinY", 50,
            "The minimum Y value that affects the Large Wind Generators Power generation.",50,255);

    public final IntOption largewindGenerationMaxY = new IntOption(this, "multiblock", "LargeWindGenerationMaxY", 255,
            "The maximum Y value that affects the Large Wind Generators Power generation.",50,255);

    public TypeConfigManager<MultiblockMachineGeneratorType> multiblockmachinegeneratorsManager = new TypeConfigManager<>(this, "multiblockmachinegenerators", MultiblockMachineGeneratorType.class,
            MultiblockMachineGeneratorType::getGeneratorsForConfig, MultiblockMachineGeneratorType::getBlockName);
}
