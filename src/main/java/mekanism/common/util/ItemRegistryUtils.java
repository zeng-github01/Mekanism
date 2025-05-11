package mekanism.common.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Locale;
import java.util.Map;

public final class ItemRegistryUtils {

    private static final Map<String, String> modIDMap = new Object2ObjectOpenHashMap<>();

    private static void populateMap() {
        Loader.instance().getIndexedModList().forEach((key, value) -> modIDMap.put(key.toLowerCase(Locale.ROOT), value.getName()));
    }

    /* Mod ID lookup thanks to JEI */
    public static String getMod(ItemStack stack) {
        if (stack.isEmpty()) {
            return "null";
        }

        if (modIDMap.isEmpty()) {
            populateMap();
        }

        ResourceLocation itemResourceLocation = Item.REGISTRY.getNameForObject(stack.getItem());

        if (itemResourceLocation == null) {
            return "null";
        }

        String modId = itemResourceLocation.getNamespace();
        String lowercaseModId = modId.toLowerCase(Locale.ENGLISH);
        return modIDMap.computeIfAbsent(lowercaseModId, k -> WordUtils.capitalize(modId));
    }
}
