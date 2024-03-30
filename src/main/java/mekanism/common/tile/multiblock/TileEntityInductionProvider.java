package mekanism.common.tile.multiblock;

import io.netty.buffer.ByteBuf;
import mekanism.api.TileNetworkList;
import mekanism.common.tier.InductionProviderTier;
import mekanism.common.tile.prefab.TileEntityBasicBlock;
import mekanism.common.util.LangUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class TileEntityInductionProvider extends TileEntityBasicBlock {

    public InductionProviderTier tier = InductionProviderTier.BASIC;

    @Override
    public void onUpdate() {
    }

    public String getName() {
        return LangUtils.localize(getBlockType().getTranslationKey() + ".InductionProvider" + tier.getBaseTier().getSimpleName() + ".name");
    }

    @Override
    public void handlePacketData(ByteBuf dataStream) {
        super.handlePacketData(dataStream);
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            InductionProviderTier prevTier = tier;
            tier = InductionProviderTier.values()[dataStream.readInt()];
            if (prevTier != tier) {
                MekanismUtils.updateBlock(world, getPos());
            }
        }
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        super.getNetworkedData(data);
        data.add(tier.ordinal());
        return data;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbtTags) {
        super.readCustomNBT(nbtTags);
        tier = InductionProviderTier.values()[nbtTags.getInteger("tier")];
    }

    @Override
   public void writeCustomNBT(NBTTagCompound nbtTags) {
        super.writeCustomNBT(nbtTags);
        nbtTags.setInteger("tier", tier.ordinal());
    }
}
