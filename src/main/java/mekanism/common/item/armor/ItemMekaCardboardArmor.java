package mekanism.common.item.armor;

import mekanism.api.EnumColor;
import mekanism.client.MekKeyHandler;
import mekanism.client.MekanismKeyHandler;
import mekanism.client.model.ModelPackage;
import mekanism.common.CardboardArmorHandler;
import mekanism.common.Mekanism;
import mekanism.common.MekanismItems;
import mekanism.common.integration.MekanismHooks;
import mekanism.common.util.LangUtils;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemMekaCardboardArmor extends ItemArmor {

    public static ArmorMaterial MekaCardboard = EnumHelper.addArmorMaterial("mekcardboard", "mekanism:cardboard", 4, new int[]{1, 1, 1, 1}, 25, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F).setRepairItem(new ItemStack(MekanismItems.Sawdust));

    public ItemMekaCardboardArmor(int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(MekaCardboard, renderIndexIn, equipmentSlotIn);
        setMaxStackSize(1);
        setCreativeTab(Mekanism.tabMekanism);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        if (MekKeyHandler.getIsKeyPressed(MekanismKeyHandler.sneakKey)) {
            tooltip.add(EnumColor.DARK_GREY + LangUtils.localize("tooltip.hold") + " " + EnumColor.GREY + GameSettings.getKeyDisplayString(MekanismKeyHandler.sneakKey.getKeyCode()) + EnumColor.DARK_GREY + " " + LangUtils.localize("tooltip.holdForDescription") + ".");
            tooltip.add("");
            tooltip.add(LangUtils.localize("item.mekanism.cardboard_armor.tooltip.summary"));
            tooltip.add("");
            tooltip.add(LangUtils.localize("item.mekanism.cardboard_armor.tooltip.condition1"));
            tooltip.add(LangUtils.localize("item.mekanism.cardboard_armor.tooltip.behaviour1"));
        } else {
            if (!Loader.isModLoaded(MekanismHooks.MekanismMixinHelp_MOD_ID)) {
                tooltip.add(EnumColor.ORANGE + LangUtils.localize("tooltip.mekanism.warning"));
                tooltip.add(EnumColor.ORANGE + LangUtils.localize("need.installation.mod"));
            } else {
                tooltip.add(EnumColor.ORANGE + LangUtils.localize("tooltip.mekanism.note"));
                tooltip.add(EnumColor.ORANGE + LangUtils.localize("tooltip.mekanism.note.cardboard"));
            }
            tooltip.add("");
            tooltip.add(EnumColor.GREY + LangUtils.localize("tooltip.hold") + " " + EnumColor.DARK_GREY + GameSettings.getKeyDisplayString(MekanismKeyHandler.sneakKey.getKeyCode()) + EnumColor.GREY + " " + LangUtils.localize("tooltip.holdForDescription") + ".");
        }
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        int layer = slot == EntityEquipmentSlot.LEGS ? 2 : 1;
        if (CardboardArmorHandler.testForStealth(entity)) {
            return "mekanism:render/Package.png";
        }
        return "mekanism:armor/cardboard_layer_" + layer + ".png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        if (CardboardArmorHandler.testForStealth(entityLiving)) {
            return ModelPackage.pack;
        }
        return super.getArmorModel(entityLiving, itemStack, armorSlot, _default);
    }


    @Override
    public int getItemBurnTime(ItemStack itemStack) {
        return 1000;
    }

}
