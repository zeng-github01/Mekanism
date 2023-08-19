package mekanism.tools.common;

import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;


public class CreativeTabMekanismTools extends CreativeTabs {

    public CreativeTabMekanismTools() {
        super("tabMekanismTools");
    }

    @Nonnull
    @Override
    public ItemStack createIcon() {
        return new ItemStack(ToolsItem.STEEL_PAXEL.getItem());
    }

}
