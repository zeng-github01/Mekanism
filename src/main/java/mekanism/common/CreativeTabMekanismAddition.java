package mekanism.common;

import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTabMekanismAddition extends CreativeTabs {

    public CreativeTabMekanismAddition() {
        super("tabMekanismAddition");
    }

    @Nonnull
    @Override
    public ItemStack createIcon() {
        return new ItemStack(MekanismItems.WalkieTalkie);
    }
}
