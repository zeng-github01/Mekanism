package mekanism.common;

import mekanism.common.recipe.RecipeHandler;
import mekanism.common.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AutoMekanismRecipes {

    /**
     * copy COFHCOFH code
     */
    public static String titleCase(String input) {
        return input.substring(0, 1).toUpperCase(Locale.US) + input.substring(1);
    }

    public static boolean oreNameExists(String oreName) {
        return OreDictionary.doesOreNameExist(oreName) && !OreDictionary.getOres(oreName, false).isEmpty();
    }

    public static ItemStack getStringinItem(String s) {
        return StackUtils.size(OreDictionary.getOres(s, false).get(0), 1);
    }

    public static List<ItemStack> Input(String input, String input2) {
        List<ItemStack> stackList = new ArrayList<>();
        if (input != null && !input.isEmpty() && input2 != null && !input2.isEmpty()) {
            stackList.addAll(OreDictionary.getOres(input + titleCase(input2)));
        }
        return stackList;
    }


    public static String ingot = "ingot"; //锭
    public static String plate = "plate"; //板
    public static String gear = "gear"; //齿轮
    public static String stick = "stick"; //杆

    /**
     * Stamping
     */

    public static void addStampingRecipe() { // 冲床 齿轮
        String[] oreNames = OreDictionary.getOreNames();
        String oreType;
        for (String oreName : oreNames) {
            if (oreName.startsWith(gear)) {
                oreType = oreName.substring(4);
                addStampingRecipe(oreType, gear);
            }
        }
    }

    private static void addStampingRecipe(String oreType, String string) {
        if (oreType == null || oreType.isEmpty()) {
            return;
        }
        String gearName = string + titleCase(oreType);
        if (!oreNameExists(gearName)) {
            return;
        }
        addStampingRecipe(oreType, getStringinItem(gearName));
    }

    private static void addStampingRecipe(String oreType, ItemStack gear) {
        for (ItemStack input : Input(ingot, oreType)) {
            if (input.isEmpty()) {
                continue;
            }
            RecipeHandler.addStampingRecipe(StackUtils.size(input, 4), gear);
        }
        for (ItemStack input : Input("gem", oreType)) {
            if (input.isEmpty()) {
                continue;
            }
            RecipeHandler.addStampingRecipe(StackUtils.size(input, 4), gear);
        }
    }


    /**
     * Rolling
     */

    public static void addRollingRecipe() { //轧制机 板
        String[] oreNames = OreDictionary.getOreNames();
        String oreType;
        for (String oreName : oreNames) {
            if (oreName.startsWith(plate)) {
                oreType = oreName.substring(5);
                addRollingRecipe(oreType, plate);
            }
        }
    }

    private static void addRollingRecipe(String oreType, String string) {
        if (oreType == null || oreType.isEmpty()) {
            return;
        }
        String gearName = string + titleCase(oreType);
        if (!oreNameExists(gearName)) {
            return;
        }
        addRollingRecipe(oreType, getStringinItem(gearName));
    }


    private static void addRollingRecipe(String oreType, ItemStack gear) {
        for (ItemStack input : Input(ingot, oreType)) {
            if (input.isEmpty()) {
                continue;
            }
            RecipeHandler.addRollingRecipe(StackUtils.size(input, 1), gear);
        }
        for (ItemStack input : Input("gem", oreType)) {
            if (input.isEmpty()) {
                continue;
            }
            RecipeHandler.addRollingRecipe(StackUtils.size(input, 1), gear);
        }
    }

    /**
     * Turning
     */

    public static void addTurningRecipe() {  // 车床 杆
        String[] oreNames = OreDictionary.getOreNames();
        String oreType;
        for (String oreName : oreNames) {
            if (oreName.startsWith(stick)) {
                oreType = oreName.substring(5);
                addTurningRecipe(oreType, stick);
            }
        }
    }

    private static void addTurningRecipe(String oreType, String string) {
        if (oreType == null || oreType.isEmpty()) {
            return;
        }
        String gearName = string + titleCase(oreType);
        if (!oreNameExists(gearName)) {
            return;
        }
        addTurningRecipe(oreType, getStringinItem(gearName));
    }


    private static void addTurningRecipe(String oreType, ItemStack gear) {
        for (ItemStack input : Input(ingot, oreType)) {
            if (input.isEmpty()) {
                continue;
            }
            RecipeHandler.addTurningRecipe(StackUtils.size(input, 1), StackUtils.size(gear, 4));
        }
    }
}
