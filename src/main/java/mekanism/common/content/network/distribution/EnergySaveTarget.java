package mekanism.common.content.network.distribution;

import mcp.MethodsReturnNonnullByDefault;
import mekanism.api.energy.IEnergizedItem;

import mekanism.common.lib.distribution.SplitInfo;
import mekanism.common.lib.distribution.Target;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

public class EnergySaveTarget extends Target<EnergySaveTarget.SaveHandler,Double,Double> {

    public EnergySaveTarget(int expectedSize) {
        super(expectedSize);
    }

    @Override
    protected void acceptAmount(SaveHandler handler, SplitInfo<Double> splitInfo, Double amount) {
        handler.acceptAmount(splitInfo, amount);
    }

    @Override
    protected Double simulate(SaveHandler handler, Double energyToSend) {
        return handler.simulate(energyToSend);
    }

    public void save() {
        for (SaveHandler handler : handlers) {
            handler.save();
        }
    }

    public void addDelegate(ItemStack delegate) {
        this.addHandler(new SaveHandler(delegate));
    }

    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    public static class SaveHandler {
        private final ItemStack delegate;
        private double currentStored = 0;

        public SaveHandler(ItemStack delegate) {
            this.delegate = delegate;
        }

        protected void acceptAmount(SplitInfo<Double> splitInfo, double amount) {
            if (delegate.getItem() instanceof IEnergizedItem item) {
                amount = Math.min(amount, item.getMaxEnergy(delegate)) - currentStored;
                currentStored += amount;
                splitInfo.send(amount);
            }
        }

        protected double simulate(double energyToSend) {
            if (delegate.getItem() instanceof IEnergizedItem item) {
                return Math.min(energyToSend, item.getMaxEnergy(delegate) - currentStored);
            }
            return 0;
        }

        protected void save() {
            if (delegate.getItem() instanceof IEnergizedItem item) {
                item.setEnergy(delegate, currentStored);
            }
        }
    }
}
