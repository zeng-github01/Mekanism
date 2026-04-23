package mekanism.common.util;

import mekanism.common.Mekanism;

public final class BloomDependencyHelper {

    private static boolean warnedUnavailable;

    private BloomDependencyHelper() {
    }

    public static void disableBloom(String failingComponent) {
        disableBloom(failingComponent, null);
    }

    public static void disableBloom(String failingComponent, Throwable throwable) {
        Mekanism.hooks.Bloom = false;
        if (!warnedUnavailable) {
            warnedUnavailable = true;
            if (throwable == null) {
                Mekanism.logger.warn("Disabling Mekanism bloom integration because required bloom classes are unavailable. First failing component: {}", failingComponent);
            } else {
                Mekanism.logger.warn("Disabling Mekanism bloom integration because required bloom classes are unavailable. First failing component: {}", failingComponent, throwable);
            }
        }
    }
}
