package mekanism.common.integration.farmersdelightlegacy;

import com.wdcftgg.farmersdelightlegacy.api.heat.HeatSourceApi;
import mekanism.api.IHeatTransfer;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.util.CapabilityUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class FarmersDelightLegacyIntegration {

    private static final String HEAT_SOURCE_KEY = "mekanism:heat_transfer";

    private FarmersDelightLegacyIntegration() {
    }

    public static void init() {
        HeatSourceApi.registerDirectHeatSourcePredicate(HEAT_SOURCE_KEY, FarmersDelightLegacyIntegration::isMekanismHeatSource);
    }

    private static boolean isMekanismHeatSource(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile == null) {
            return false;
        }
        IHeatTransfer heatTransfer = CapabilityUtils.getCapability(tile, Capabilities.HEAT_TRANSFER_CAPABILITY, null);
        // Mekanism heat values are stored as temperature above ambient, so any positive value is hot enough.
        return heatTransfer != null && heatTransfer.getTemp() > 0;
    }
}
