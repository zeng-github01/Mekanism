package mekanism.common.content.gear.mekasuit;

import baubles.api.BaublesApi;
import cofh.redstoneflux.api.IEnergyContainerItem;
import ic2.api.item.ElectricItem;
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

import java.util.ArrayList;
import java.util.List;

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
        EmitUtils2.sendToAcceptors(saveTarget, total);
        saveTarget.save();
    }

    private void chargeInventory(IModule<ModuleChargeDistributionUnit> module, EntityPlayer player) {
        List<ItemStack> stacks = new ArrayList<>();
        stacks.addAll(player.inventory.offHandInventory);
        stacks.addAll(player.inventory.mainInventory);
        if (Mekanism.hooks.Baubles) {
            stacks.addAll(chargeBaublesInventory(player));
        }
        for (ItemStack stack : stacks) {
            if (canCharge(module, player, stack)) {
                charge(stack, module, player);
            }
            if (module.getContainerEnergy() <= 0) {
                break;
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

    private boolean canCharge(IModule<ModuleChargeDistributionUnit> module, EntityPlayer player, ItemStack stack) {
        if (!stack.isEmpty()) {
            return canCharge(stack, module, player);
        }
        return false;
    }

    //也许这个不行
    public void charge(ItemStack stack, IModule<ModuleChargeDistributionUnit> module, EntityPlayer player) {
        if (!stack.isEmpty() && module.getContainerEnergy() > 0) {
            if (stack.getItem() instanceof IEnergizedItem) {
                module.useEnergy(player, EnergizedItemManager.charge(stack, module.getContainerEnergy()));
            } else if (MekanismUtils.useTesla() && stack.hasCapability(Capabilities.TESLA_CONSUMER_CAPABILITY, null)) {
                ITeslaConsumer consumer = stack.getCapability(Capabilities.TESLA_CONSUMER_CAPABILITY, null);
                long stored = TeslaIntegration.toTesla(module.getContainerEnergy());
                module.useEnergy(player, TeslaIntegration.fromTesla(consumer.givePower(stored, false)));
            } else if (MekanismUtils.useForge() && stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
                IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
                if (storage.canReceive()) {
                    int stored = ForgeEnergyIntegration.toForge(module.getContainerEnergy());
                    module.useEnergy(player, ForgeEnergyIntegration.fromForge(storage.receiveEnergy(stored, false)));
                }
            } else if (MekanismUtils.useRF() && stack.getItem() instanceof IEnergyContainerItem item) {
                int toTransfer = RFIntegration.toRF(module.getContainerEnergy());
                module.useEnergy(player, RFIntegration.fromRF(item.receiveEnergy(stack, toTransfer, false)));
            } else if (MekanismUtils.useIC2() && isIC2Chargeable(stack)) {
                double sent = IC2Integration.fromEU(ElectricItem.manager.charge(stack, IC2Integration.toEU(module.getContainerEnergy()), 4, true, false));
                module.useEnergy(player, sent);
            }
        }
    }

    public boolean canCharge(ItemStack stack, IModule<ModuleChargeDistributionUnit> module, EntityPlayer player) {
        if (!stack.isEmpty() && module.getContainerEnergy() > 0) {
            if (stack.getItem() instanceof IEnergizedItem) {
                return module.canUseEnergy(player, EnergizedItemManager.charge(stack, module.getContainerEnergy()));
            } else if (MekanismUtils.useTesla() && stack.hasCapability(Capabilities.TESLA_CONSUMER_CAPABILITY, null)) {
                ITeslaConsumer consumer = stack.getCapability(Capabilities.TESLA_CONSUMER_CAPABILITY, null);
                long stored = TeslaIntegration.toTesla(module.getContainerEnergy());
                return module.canUseEnergy(player, TeslaIntegration.fromTesla(consumer.givePower(stored, false)));
            } else if (MekanismUtils.useForge() && stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
                IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
                if (storage.canReceive()) {
                    int stored = ForgeEnergyIntegration.toForge(module.getContainerEnergy());
                    return module.canUseEnergy(player, ForgeEnergyIntegration.fromForge(storage.receiveEnergy(stored, false)));
                }
                return false;
            } else if (MekanismUtils.useRF() && stack.getItem() instanceof IEnergyContainerItem item) {
                int toTransfer = RFIntegration.toRF(module.getContainerEnergy());
                return module.canUseEnergy(player, RFIntegration.fromRF(item.receiveEnergy(stack, toTransfer, false)));
            } else if (MekanismUtils.useIC2() && isIC2Chargeable(stack)) {
                double sent = IC2Integration.fromEU(ElectricItem.manager.charge(stack, IC2Integration.toEU(module.getContainerEnergy()), 4, true, false));
                return module.canUseEnergy(player, sent);
            }
        }
        return false;
    }
}
