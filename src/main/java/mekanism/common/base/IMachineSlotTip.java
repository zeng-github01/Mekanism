package mekanism.common.base;

public interface IMachineSlotTip {


    boolean getEnergySlot();

    boolean getInputSlot();

    boolean getOuputSlot();

    default boolean getExtraSlot(){
        return false;
    }
}
