package mekanism.api;

import mekanism.api.math.MathUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.translation.I18n;

public enum RelativeSide {
    BOTTOM("sideData.bottom"),
    TOP("sideData.top"),
    FRONT("sideData.front"),
    BACK("sideData.back"),
    LEFT("sideData.left"),
    RIGHT("sideData.right");

    public String name;
    public static final RelativeSide[] SIDES = values();

    public static RelativeSide byIndex(int index) {
        return MathUtils.getByIndexMod(SIDES, index);
    }

    RelativeSide(String sideData) {
        name = sideData;
    }

    public String getTranslationKey(){
        return I18n.translateToLocal("sideData." + name);
    }

    public EnumFacing getEnumFacing(EnumFacing facing) {
        return switch (this) {
            case FRONT -> facing;
            case BACK -> facing.getOpposite();
            case LEFT -> facing == EnumFacing.DOWN || facing == EnumFacing.UP ? EnumFacing.EAST : facing.rotateY();
            case RIGHT -> facing == EnumFacing.DOWN || facing == EnumFacing.UP ? EnumFacing.WEST : facing.rotateYCCW();
            case TOP -> switch (facing) {
                case DOWN -> EnumFacing.NORTH;
                case UP -> EnumFacing.SOUTH;
                default -> EnumFacing.UP;
            };
            case BOTTOM -> switch (facing) {
                case DOWN -> EnumFacing.SOUTH;
                case UP -> EnumFacing.NORTH;
                default -> EnumFacing.DOWN;
            };
        };
    }


    public static RelativeSide fromDirections(EnumFacing facing, EnumFacing side) {
        if (side == facing) {
            return FRONT;
        } else if (side == facing.getOpposite()) {
            return BACK;
        } else if (facing == EnumFacing.DOWN || facing == EnumFacing.UP) {
            return switch (side) {
                case NORTH -> facing == EnumFacing.DOWN ? TOP : BOTTOM;
                case SOUTH -> facing == EnumFacing.DOWN ? BOTTOM : TOP;
                case WEST -> RIGHT;
                case EAST -> LEFT;
                default -> throw new IllegalStateException("Case should have been caught earlier.");
            };
        } else if (side == EnumFacing.DOWN) {
            return BOTTOM;
        } else if (side == EnumFacing.UP) {
            return TOP;
        } else if (side == facing.rotateYCCW()) {
            return RIGHT;
        } else if (side == facing.rotateY()) {
            return LEFT;
        }
        //Fall back to front, should never get here
        return FRONT;
    }
}
