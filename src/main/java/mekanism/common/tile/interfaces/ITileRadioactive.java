package mekanism.common.tile.interfaces;

import mekanism.api.MekanismAPI;
import mekanism.api.gas.GasTankInfo;
import mekanism.api.gas.IGasHandler;
import mekanism.api.math.MathUtils;

public interface ITileRadioactive {

    static float calculateRadiationScale(GasTankInfo[] tanks) {
        if (MekanismAPI.getRadiationManager().isRadiationEnabled() && tanks != IGasHandler.NONE) {
            float summedScale = 0;
            for (GasTankInfo tank : tanks) {
                if (tank != null && tank.getGas() != null && tank.getGas().getGas() != null && tank.getGas().getGas().isRadiation()) {
                    //TODO: Eventually we may want to debate doing this based on the radioactivity
                    // but for now this will work well
                    summedScale += tank.getStored() / (float) tank.getMaxGas();
                }
            }
            return summedScale / tanks.length;
        }
        return 0;
    }

    float getRadiationScale();

    default int getRadiationParticleCount() {
        return MathUtils.clampToInt(10 * getRadiationScale());
    }
}
