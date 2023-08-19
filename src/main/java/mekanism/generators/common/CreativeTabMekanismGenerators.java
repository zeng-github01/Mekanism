package mekanism.generators.common;

import javax.annotation.Nonnull;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;


public class CreativeTabMekanismGenerators extends CreativeTabs {

    public CreativeTabMekanismGenerators() {
        super("tabMekanismGenerators");
    }

    @Nonnull
    @Override
    public ItemStack createIcon() {
        return new ItemStack(GeneratorsBlocks.Generator, 1, 6);
    }

}
