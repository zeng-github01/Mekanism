package mekanism.multiblockmachine.common.tile.machine.prefab;

import mekanism.common.Upgrade;
import mekanism.common.base.IElectricMachine;
import mekanism.common.integration.computer.IComputerIntegration;
import mekanism.common.recipe.inputs.MachineInput;
import mekanism.common.recipe.machines.MachineRecipe;
import mekanism.common.recipe.outputs.MachineOutput;
import mekanism.common.util.MekanismUtils;
import mekanism.multiblockmachine.common.block.states.BlockStateMultiblockMachine;

public abstract class TileEntityMultiblockBasicMachine<INPUT extends MachineInput<INPUT>, OUTPUT extends MachineOutput<OUTPUT>,
        RECIPE extends MachineRecipe<INPUT, OUTPUT, RECIPE>> extends TileEntityMultiblockOperationalMachine implements IElectricMachine<INPUT, OUTPUT, RECIPE>, IComputerIntegration {

    public RECIPE cachedRecipe = null;

    public TileEntityMultiblockBasicMachine(String soundPath, BlockStateMultiblockMachine.MultiblockMachineType type, int baseTicksRequired, int slot) {
        super("machine." + soundPath, type, baseTicksRequired, slot);
    }



    protected void setupVariableValues() {
    }

    protected void setUpOtherActions() {
    }

    protected void setClearOperatingTicks() {
    }

    protected void setFinish(){

    }

    protected void setNoFinish(){

    }

    public void getProcess(RECIPE recipe) {
        getProcess(recipe, true);
    }

    public void getProcess(RECIPE recipe, boolean canOperate) {
        getProcess(recipe, canOperate, energyPerTick, true, true);
    }

    public void getProcess(RECIPE recipe, boolean canOperate, double energyTick) {
        getProcess(recipe, canOperate, energyTick, true, true);
    }



    public void getProcess(RECIPE recipe, boolean canOperate, double energyTick, boolean clear, boolean defaultEnergy) {
        if (canOperate(recipe) && MekanismUtils.canFunction(this) && getEnergy() >= energyTick && canOperate) {
            setupVariableValues();
            setActive(true);
            operatingTicks++;
            if (defaultEnergy) {
                electricityStored.addAndGet(-energyTick);
            }
            if (operatingTicks >= ticksRequired) {
                MultipleActions(recipe);
                operatingTicks = 0;
                setFinish();
            }
            setUpOtherActions();
        } else{
            setNoFinish();
            if (prevEnergy >= getEnergy()) {
                setActive(false);
            }
        }
        if (clear) {
            if (!canOperate(recipe)) {
                operatingTicks = 0;
            }
        } else {
            setClearOperatingTicks();
        }
    }

    protected void MultipleActions(RECIPE recipe) {
        if (upgradeComponent.isUpgradeInstalled(Upgrade.THREAD)){
            for (int i = 0; i <= Thread(); i++) {
                if (!canOperate(recipe)){
                    break;
                }
                MultipleActions(recipe, ticksRequired);
            }
        }else {
            MultipleActions(recipe, ticksRequired);
        }
    }

}
