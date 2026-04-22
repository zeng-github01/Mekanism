package mekanism.common.inventory.container;

import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SelectedWindowData {

    private static final Map<String, Pair<Integer, Integer>> LAST_POSITIONS = new HashMap<>();

    public static final SelectedWindowData UNSPECIFIED = new SelectedWindowData(WindowType.UNSPECIFIED);

    @Nonnull
    public final WindowType type;
    public final byte extraData;

    public SelectedWindowData(@Nonnull WindowType type) {
        this(type, (byte) 0);
    }

    public SelectedWindowData(@Nonnull WindowType type, byte extraData) {
        this.type = Objects.requireNonNull(type);
        this.extraData = this.type.isValid(extraData) ? extraData : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SelectedWindowData other = (SelectedWindowData) o;
        return extraData == other.extraData && type == other.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, extraData);
    }

    public void updateLastPosition(int x, int y) {
        String saveName = type.getSaveName(extraData);
        if (saveName != null) {
            LAST_POSITIONS.put(saveName, Pair.of(x, y));
        }
    }

    public Pair<Integer, Integer> getLastPosition() {
        String saveName = type.getSaveName(extraData);
        if (saveName != null) {
            Pair<Integer, Integer> cachedPosition = LAST_POSITIONS.get(saveName);
            if (cachedPosition != null) {
                return cachedPosition;
            }
        }
        return Pair.of(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public enum WindowType {
        COLOR("color"),
        CONFIRMATION("confirmation"),
        MEKA_SUIT_HELMET("mekaSuitHelmet"),
        RENAME("rename"),
        SKIN_SELECT("skinSelect"),
        SIDE_CONFIG("sideConfig"),
        TRANSPORTER_CONFIG("transporterConfig"),
        UPGRADE("upgrade"),
        UNSPECIFIED(null);

        @Nullable
        private final String saveName;
        private final byte maxData;

        WindowType(@Nullable String saveName) {
            this(saveName, (byte) 1);
        }

        WindowType(@Nullable String saveName, byte maxData) {
            this.saveName = saveName;
            this.maxData = maxData;
        }

        @Nullable
        String getSaveName(byte extraData) {
            return maxData == 1 || saveName == null ? saveName : saveName + extraData;
        }

        public List<String> getSavePaths() {
            if (saveName == null) {
                return Collections.emptyList();
            } else if (maxData == 1) {
                return Collections.singletonList(saveName);
            }
            List<String> savePaths = new ArrayList<>();
            for (int i = 0; i < maxData; i++) {
                savePaths.add(saveName + i);
            }
            return savePaths;
        }

        public boolean isValid(byte extraData) {
            return extraData >= 0 && extraData < maxData;
        }
    }
}
