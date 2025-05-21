package mekanism.common.content.gear.shared;

import mekanism.api.annotations.ParametersAreNotNullByDefault;
import mekanism.api.gear.ICustomModule;
import mekanism.api.gear.IModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

@ParametersAreNotNullByDefault
public class ModuleMagneticUnit implements ICustomModule<ModuleMagneticUnit> {


    @Override
    public void tickEntityClient(IModule<ModuleMagneticUnit> module, Entity item) {
        tickEntityServer(module, item);
    }

    @Override
    public void tickEntityServer(IModule<ModuleMagneticUnit> module, Entity item) {
        addInventoryItem(item);
    }

    private void addInventoryItem(Entity entity) {
        if (entity instanceof EntityItem item) {
            ItemStack stack = item.getItem();
            EntityPlayer player = item.world.getClosestPlayerToEntity(item, 8.0);
            if (player != null && player.isEntityAlive() && !stack.isEmpty()) {
                //如果物品不是盔甲或者无法插入到盔甲栏
                boolean canAddArmorInventory = stack.getItem() instanceof ItemArmor && addArmorInventory(player, stack, item);
                //插入到背包
                if (!canAddArmorInventory) {
                    //判断能否插入到主手？
                    boolean canAddHeldItem = addHeldItem(player, stack, item);
                    //如果不能插入主手，则插入背包？
                    if (!canAddHeldItem) {
                        addBackpack(player, stack, item);
                    }
                }
            }
        }
    }

    //添加物品到盔甲栏
    private boolean addArmorInventory(EntityPlayer player, ItemStack stack, EntityItem item) {
        //检查物品是不是盔甲
        if (stack.getItem() instanceof ItemArmor armor) {
            EntityEquipmentSlot slot = armor.armorType;
            //获取盔甲对应的插槽
            if (player.getItemStackFromSlot(slot).isEmpty()) {
                player.setItemStackToSlot(slot, stack);
                item.setDead();
                return true;
            }
            return false;
        }
        return false;
    }

    private boolean addHeldItem(EntityPlayer player, ItemStack stack, EntityItem item) {
        //检查玩家主手是不是空的
        if (player.getHeldItemMainhand().isEmpty()) {
            player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack);
            item.setDead();
            return true;
        }
        return false;
    }


    private void addBackpack(EntityPlayer player, ItemStack stack, EntityItem item) {
        int backpackEmpty = player.inventory.getFirstEmptyStack();
        if (backpackEmpty >= 0) {
            player.inventory.mainInventory.set(backpackEmpty, stack);
            player.inventory.mainInventory.get(backpackEmpty).setAnimationsToGo(5);
            item.setDead();
        }
    }
}
