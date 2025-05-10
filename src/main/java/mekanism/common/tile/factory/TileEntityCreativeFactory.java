package mekanism.common.tile.factory;

import mekanism.common.block.states.BlockStateMachine;
import mekanism.common.tier.FactoryTier;

public class TileEntityCreativeFactory extends TileEntityFactory {

    public TileEntityCreativeFactory() {
        super(FactoryTier.CREATIVE, BlockStateMachine.MachineType.CREATIVE_FACTORY,0);
    }
}
