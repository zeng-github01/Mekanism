package mekanism.common.tile;

import mekanism.api.EnumColor;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.SideData;
import mekanism.common.block.states.BlockStateMachine.MachineType;
import mekanism.common.tier.FactoryTier;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.util.InventoryUtils;

public class TileEntityAdvancedFactory extends TileEntityFactory {

    public TileEntityAdvancedFactory() {
        super(FactoryTier.ADVANCED, MachineType.ADVANCED_FACTORY);

        configComponent = new TileComponentConfig(this, TransmissionType.ITEM, TransmissionType.ENERGY, TransmissionType.GAS);

        configComponent.addOutput(TransmissionType.ITEM, new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
        configComponent.addOutput(TransmissionType.ITEM, new SideData("Input", EnumColor.RED, new int[]{5, 6, 7, 8, 9}));
        configComponent.addOutput(TransmissionType.ITEM, new SideData("Output", EnumColor.INDIGO, new int[]{10, 11, 12, 13, 14, 15, 16, 17, 18, 19}));
        configComponent.addOutput(TransmissionType.ITEM, new SideData("Energy", EnumColor.BRIGHT_GREEN, new int[]{1}));
        configComponent.addOutput(TransmissionType.ITEM, new SideData("Extra", EnumColor.ORANGE, new int[]{4}));
        configComponent.setConfig(TransmissionType.ITEM, new byte[]{4, 1, 1, 3, 1, 2});

        configComponent.addOutput(TransmissionType.GAS, new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
        configComponent.addOutput(TransmissionType.GAS, new SideData("Input", EnumColor.RED, new int[]{0}));
        configComponent.addOutput(TransmissionType.GAS, new SideData("Output", EnumColor.AQUA, new int[]{1}));
        configComponent.fillConfig(TransmissionType.GAS, 1);

        configComponent.setInputConfig(TransmissionType.ENERGY);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(TransmissionType.ITEM, configComponent.getOutputs(TransmissionType.ITEM).get(2));
        ejectorComponent.setOutputData(TransmissionType.GAS, configComponent.getOutputs(TransmissionType.GAS).get(2));
    }
}
