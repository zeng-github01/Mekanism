package mekanism.common;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import mekanism.api.EnumColor;
import mekanism.common.base.IUpgradeTile;
import mekanism.common.config.MekanismConfig;
import mekanism.common.util.LangUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants.NBT;

public enum Upgrade {
    SPEED("speed", MekanismConfig.current().mekce.MAXSpeedUpgrade.val(), MekanismConfig.current().mekce.MAXSpeedUpgradeSize.val(), EnumColor.RED),
    ENERGY("energy", MekanismConfig.current().mekce.MAXEnergyUpgrade.val(), MekanismConfig.current().mekce.MAXEnergyUpgradeSize.val(), EnumColor.BRIGHT_GREEN),
    FILTER("filter", 1, 1, EnumColor.DARK_AQUA),
    GAS("gas", MekanismConfig.current().mekce.MAXGasUpgrade.val(), MekanismConfig.current().mekce.MAXGasUpgradeSize.val(), EnumColor.YELLOW),
    MUFFLING("muffling", MekanismConfig.current().mekce.MAXMufflingUpgrade.val(), MekanismConfig.current().mekce.MAXMufflingUpgradeSize.val(), EnumColor.DARK_GREY),
    ANCHOR("anchor", 1, 1, EnumColor.DARK_GREEN);

    private String name;
    private int maxStack;
    private int maxItemStack;
    private EnumColor color;

    Upgrade(String s, int max, int maxItem, EnumColor c) {
        name = s;
        maxStack = max;
        maxItemStack = maxItem;
        color = c;
    }

    public static Map<Upgrade, Integer> buildMap(@Nullable NBTTagCompound nbtTags) {
        Map<Upgrade, Integer> upgrades = new EnumMap<>(Upgrade.class);
        if (nbtTags != null) {
            if (nbtTags.hasKey("upgrades")) {
                NBTTagList list = nbtTags.getTagList("upgrades", NBT.TAG_COMPOUND);
                for (int tagCount = 0; tagCount < list.tagCount(); tagCount++) {
                    NBTTagCompound compound = list.getCompoundTagAt(tagCount);
                    Upgrade upgrade = Upgrade.values()[compound.getInteger("type")];
                    upgrades.put(upgrade, compound.getInteger("amount"));
                }
            }
        }
        return upgrades;
    }

    public static void saveMap(Map<Upgrade, Integer> upgrades, NBTTagCompound nbtTags) {
        NBTTagList list = new NBTTagList();
        for (Entry<Upgrade, Integer> entry : upgrades.entrySet()) {
            list.appendTag(getTagFor(entry.getKey(), entry.getValue()));
        }
        nbtTags.setTag("upgrades", list);
    }

    public static NBTTagCompound getTagFor(Upgrade upgrade, int amount) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("type", upgrade.ordinal());
        compound.setInteger("amount", amount);
        return compound;
    }

    public String getName() {
        return LangUtils.localize("upgrade." + name);
    }

    public String getDescription() {
        return LangUtils.localize("upgrade." + name + ".desc");
    }

    public int getItemMax() {
        return maxItemStack;
    }

    public int getMax() {
        return maxStack;
    }

    public EnumColor getColor() {
        return color;
    }

    public boolean canMultiply() {
        return getMax() > 1;
    }

    public ItemStack getStack() {
        switch (this) {
            case SPEED:
                return new ItemStack(MekanismItems.SpeedUpgrade);
            case ENERGY:
                return new ItemStack(MekanismItems.EnergyUpgrade);
            case FILTER:
                return new ItemStack(MekanismItems.FilterUpgrade);
            case MUFFLING:
                return new ItemStack(MekanismItems.MufflingUpgrade);
            case GAS:
                return new ItemStack(MekanismItems.GasUpgrade);
            case ANCHOR:
                return new ItemStack(MekanismItems.AnchorUpgrade);
        }
        return ItemStack.EMPTY;
    }

    public List<String> getInfo(TileEntity tile) {
        List<String> ret = new ArrayList<>();
        if (tile instanceof IUpgradeTile) {
            if (tile instanceof IUpgradeInfoHandler) {
                return ((IUpgradeInfoHandler) tile).getInfo(this);
            } else {
                ret = getMultScaledInfo((IUpgradeTile) tile);
            }
        }
        return ret;
    }

    public List<String> getMultScaledInfo(IUpgradeTile tile) {
        List<String> ret = new ArrayList<>();
        if (canMultiply()) {
            double effect = Math.pow(MekanismConfig.current().general.maxUpgradeMultiplier.val(), (float) tile.getComponent().getUpgrades(this) / (float) getMax());
            ret.add(LangUtils.localize("gui.upgrades.effect") + ": " + (Math.round(effect * 100) / 100F) + "x");
        }
        return ret;
    }

    public List<String> getExpScaledInfo(IUpgradeTile tile) {
        List<String> ret = new ArrayList<>();
        if (canMultiply()) {
            double effect = Math.min(Math.pow(2, (float) tile.getComponent().getUpgrades(this)), MekanismConfig.current().mekce.MAXspeedmachines.val());
            ret.add(LangUtils.localize("gui.upgrades.effect") + ": " + effect + "x");
        }
        return ret;
    }

    public interface IUpgradeInfoHandler {

        List<String> getInfo(Upgrade upgrade);
    }
}
