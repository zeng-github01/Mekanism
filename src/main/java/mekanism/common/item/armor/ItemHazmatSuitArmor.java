package mekanism.common.item.armor;

import mekanism.common.Mekanism;
import mekanism.common.capabilities.ItemCapabilityWrapper;
import mekanism.common.capabilities.radiation.item.RadiationShieldingHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.EnumHelper;

import javax.annotation.Nonnull;

public class ItemHazmatSuitArmor extends ItemArmor {

    public static ArmorMaterial MekHazmat = EnumHelper.addArmorMaterial("mekhazmat", "mekanism:hazmat", 0, new int[]{0, 0, 0, 0}, 0, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.0F);

    public ItemHazmatSuitArmor(int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(MekHazmat, renderIndexIn, equipmentSlotIn);
        setMaxStackSize(1);
        setCreativeTab(Mekanism.tabMekanism);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        int layer = slot == EntityEquipmentSlot.LEGS ? 2 : 1;
        return "mekanism:armor/hazmat_layer_" + layer + ".png";
    }


    public static double getShieldingByArmor(EntityEquipmentSlot type) {
        if (type == EntityEquipmentSlot.HEAD) {
            return 0.25;
        } else if (type == EntityEquipmentSlot.CHEST) {
            return 0.4;
        } else if (type == EntityEquipmentSlot.LEGS) {
            return 0.2;
        } else if (type == EntityEquipmentSlot.FEET) {
            return 0.15;
        }
        return 0;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
        return new ItemCapabilityWrapper(stack, RadiationShieldingHandler.create(item -> getShieldingByArmor(armorType)));
    }

    @Override
    public boolean isEnchantable(@Nonnull ItemStack stack) {
        return getItemEnchantability() > 0 && super.isEnchantable(stack);
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return isEnchantable(stack) && super.isBookEnchantable(stack, book);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return isEnchantable(stack) && super.canApplyAtEnchantingTable(stack, enchantment);
    }
}
