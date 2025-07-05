package mekanism.common.base;

import mekanism.common.tile.component.TileComponentUpgrade;

public interface IUpgradeTile {

    default boolean supportsUpgrades() {
        return true;
    }

    TileComponentUpgrade getComponent();
}
