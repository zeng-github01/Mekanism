package mekanism.common.content.gear.mekasuit;

import baubles.api.BaublesApi;
import cofh.redstoneflux.api.IEnergyContainerItem;
import ic2.api.item.ElectricItem;
import java.util.ArrayList;
import java.util.List;
import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.energy.EnergizedItemManager;
import mekanism.api.energy.IEnergizedItem;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import mekanism.api.gear.config.IModuleConfigItem;
import mekanism.api.gear.config.ModuleBooleanData;
import mekanism.api.gear.config.ModuleConfigItemCreator;
import mekanism.common.Mekanism;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.network.distribution.EnergySaveTarget;
import mekanism.common.integration.MekanismHooks;
import mekanism.common.integration.forgeenergy.ForgeEnergyIntegration;
import mekanism.common.integration.ic2.IC2Integration;
import mekanism.common.integration.redstoneflux.RFIntegration;
import mekanism.common.integration.tesla.TeslaIntegration;
import mekanism.common.util.EmitUtils2;
import mekanism.common.util.MekanismUtils;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.IItemHandler;

import static mekanism.common.util.ChargeUtils.isIC2Chargeable;

@ParametersAreNotNullByDefault
public class ModuleChargeDistributionUnit implements ICustomModule<ModuleChargeDistributionUnit> {

    private IModuleConfigItem<Boolean> chargeSuit;
    private IModuleConfigItem<Boolean> chargeInventory;

    @Override
    public void init(IModule<ModuleChargeDistributionUnit> module, ModuleConfigItemCreator configItemCreator) {
        chargeSuit = configItemCreator.createConfigItem("charge_suit", MekanismLang.MODULE_CHARGE_SUIT, new ModuleBooleanData());
        chargeInventory = configItemCreator.createConfigItem("charge_inventory", MekanismLang.MODULE_CHARGE_INVENTORY, new ModuleBooleanData(false));
    }

    @Override
    public void tickServer(IModule<ModuleChargeDistributionUnit> module, EntityPlayer player) {
        // charge inventory first
        if (chargeInventory.get()) {
            chargeInventory(module, player);
        }

        // distribute suit charge next
        if (chargeSuit.get()) {
            chargeSuit(player);
        }
    }

    private void chargeSuit(EntityPlayer player) {
        double total = 0;
        EnergySaveTarget saveTarget = new EnergySaveTarget(4);
        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack.getItem() instanceof IEnergizedItem item) {
                saveTarget.addDelegate(stack);
                total += item.getEnergy(stack);
            }
        }
        if (saveTarget.getHandlerCount() > 1) {
            EmitUtils2.sendToAcceptors(saveTarget, total);
            saveTarget.save();
        }
    }

    private void chargeInventory(IModule<ModuleChargeDistributionUnit> module, EntityPlayer player) {
        IEnergizedItem energyContainer = module.getEnergyContainer();
        if (energyContainer == null) {
            return;
        }
        ItemStack container = module.getContainer();
        double toCharge = Math.min(MekanismConfig.current().meka.mekaSuitInventoryChargeRate.val(), energyContainer.getEnergy(container));
        if (toCharge <= 0) {
            return;
        }
        ItemStack mainHand = player.getHeldItemMainhand();
        ItemStack offHand = player.getHeldItemOffhand();
        toCharge = charge(energyContainer, container, mainHand, toCharge);
        toCharge = charge(energyContainer, container, offHand, toCharge);
        if (toCharge <= 0) {
            return;
        }
        List<ItemStack> stacks = new ArrayList<>(player.inventory.mainInventory);
        if (Mekanism.hooks.Baubles) {
            stacks.addAll(chargeBaublesInventory(player));
        }
        for (ItemStack stack : stacks) {
            if (stack != mainHand && stack != offHand) {
                toCharge = charge(energyContainer, container, stack, toCharge);
                if (toCharge <= 0) {
                    return;
                }
            }
        }
    }

    @Optional.Method(modid = MekanismHooks.Baubles_MOD_ID)
    public List<ItemStack> chargeBaublesInventory(EntityPlayer player) {
        IItemHandler baubles = BaublesApi.getBaublesHandler(player);
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < baubles.getSlots(); i++) {
            stacks.add(baubles.getStackInSlot(i));
        }
        return stacks;
    }

    /** return rejects */
    private double charge(IEnergizedItem energyContainer, ItemStack container, ItemStack stack, double amount) {
        if (!stack.isEmpty() && amount > 0) {
            if (stack.getItem() instanceof IEnergizedItem item) {
                double simulatedAccepted = simulateInsert(item, stack, amount);
                if (simulatedAccepted > 0) {
                    double extracted = energyContainer.extract(container, simulatedAccepted, true);
                    if (extracted > 0) {
                        double inserted = Math.min(extracted, EnergizedItemManager.charge(stack, extracted));
                        refund(energyContainer, container, extracted - inserted);
                        return getRemainder(amount, inserted);
                    }
                }
            } else if (MekanismUtils.useTesla() && stack.hasCapability(Capabilities.TESLA_CONSUMER_CAPABILITY, null)) {
                ITeslaConsumer consumer = stack.getCapability(Capabilities.TESLA_CONSUMER_CAPABILITY, null);
                if (consumer != null) {
                    double simulatedAccepted = clampAccepted(amount, TeslaIntegration.fromTesla(consumer.givePower(TeslaIntegration.toTesla(amount), true)));
                    if (simulatedAccepted > 0) {
                        double extracted = energyContainer.extract(container, simulatedAccepted, true);
                        if (extracted > 0) {
                            double inserted = clampAccepted(extracted, TeslaIntegration.fromTesla(consumer.givePower(TeslaIntegration.toTesla(extracted), false)));
                            refund(energyContainer, container, extracted - inserted);
                            return getRemainder(amount, inserted);
                        }
                    }
                }
            } else if (MekanismUtils.useForge() && stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
                IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
                if (storage != null && storage.canReceive()) {
                    double simulatedAccepted = clampAccepted(amount, ForgeEnergyIntegration.fromForge(storage.receiveEnergy(ForgeEnergyIntegration.toForge(amount), true)));
                    if (simulatedAccepted > 0) {
                        double extracted = energyContainer.extract(container, simulatedAccepted, true);
                        if (extracted > 0) {
                            double inserted = clampAccepted(extracted, ForgeEnergyIntegration.fromForge(storage.receiveEnergy(ForgeEnergyIntegration.toForge(extracted), false)));
                            refund(energyContainer, container, extracted - inserted);
                            return getRemainder(amount, inserted);
                        }
                    }
                }
            } else if (MekanismUtils.useRF() && stack.getItem() instanceof IEnergyContainerItem item) {
                double simulatedAccepted = clampAccepted(amount, RFIntegration.fromRF(item.receiveEnergy(stack, RFIntegration.toRF(amount), true)));
                if (simulatedAccepted > 0) {
                    double extracted = energyContainer.extract(container, simulatedAccepted, true);
                    if (extracted > 0) {
                        double inserted = clampAccepted(extracted, RFIntegration.fromRF(item.receiveEnergy(stack, RFIntegration.toRF(extracted), false)));
                        refund(energyContainer, container, extracted - inserted);
                        return getRemainder(amount, inserted);
                    }
                }
            } else if (MekanismUtils.useIC2() && isIC2Chargeable(stack)) {
                double simulatedAccepted = clampAccepted(amount, IC2Integration.fromEU(ElectricItem.manager.charge(stack, IC2Integration.toEU(amount), 4, true, true)));
                if (simulatedAccepted > 0) {
                    double extracted = energyContainer.extract(container, simulatedAccepted, true);
                    if (extracted > 0) {
                        double inserted = clampAccepted(extracted, IC2Integration.fromEU(ElectricItem.manager.charge(stack, IC2Integration.toEU(extracted), 4, true, false)));
                        refund(energyContainer, container, extracted - inserted);
                        return getRemainder(amount, inserted);
                    }
                }
            }
        }
        return amount;
    }

    private double simulateInsert(IEnergizedItem item, ItemStack stack, double amount) {
        if (item.canReceive(stack)) {
            return Math.min(item.getMaxTransfer(stack), Math.min(item.getMaxEnergy(stack) - item.getEnergy(stack), amount));
        }
        return 0;
    }

    private double clampAccepted(double offered, double accepted) {
        return Math.min(offered, Math.max(0, accepted));
    }

    private double getRemainder(double offered, double accepted) {
        return offered - clampAccepted(offered, accepted);
    }

    private void refund(IEnergizedItem energyContainer, ItemStack container, double amount) {
        if (amount > 0) {
            energyContainer.insert(container, amount, true);
        }
    }
}
