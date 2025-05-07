package mekanism.common.inventory.warning;

import mekanism.common.inventory.warning.WarningTracker.WarningType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BooleanSupplier;

public interface IWarningTracker {

    BooleanSupplier trackWarning(@NotNull WarningType type, @NotNull BooleanSupplier warningSupplier);

    boolean hasWarning();

    List<String> getWarnings();

    void clearTrackedWarnings();
}