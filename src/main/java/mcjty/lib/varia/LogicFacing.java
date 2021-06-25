package mcjty.lib.varia;

import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;

import static net.minecraft.util.Direction.*;

public enum LogicFacing implements IStringSerializable {
    DOWN_TONORTH("down_tonorth", NORTH, 0),
    DOWN_TOSOUTH("down_tosouth", SOUTH, 1),
    DOWN_TOWEST("down_towest", WEST, 2),
    DOWN_TOEAST("down_toeast", EAST, 3),

    UP_TONORTH("up_tonorth", NORTH, 1),
    UP_TOSOUTH("up_tosouth", SOUTH, 0),
    UP_TOWEST("up_towest", WEST, 2),
    UP_TOEAST("up_toeast", EAST, 3),

    NORTH_TOWEST("north_towest", WEST, 2),
    NORTH_TOEAST("north_toeast", EAST, 3),
    NORTH_TOUP("north_toup", UP, 0),
    NORTH_TODOWN("north_todown", DOWN, 1),

    SOUTH_TOWEST("south_towest", WEST, 2),
    SOUTH_TOEAST("south_toeast", EAST, 3),
    SOUTH_TOUP("south_toup", UP, 1),
    SOUTH_TODOWN("south_todown", DOWN, 0),

    WEST_TONORTH("west_tonorth", NORTH, 2),
    WEST_TOSOUTH("west_tosouth", SOUTH, 3),
    WEST_TOUP("west_toup", UP, 1),
    WEST_TODOWN("west_todown", DOWN, 0),

    EAST_TONORTH("east_tonorth", NORTH, 3),
    EAST_TOSOUTH("east_tosouth", SOUTH, 2),
    EAST_TOUP("east_toup", UP, 1),
    EAST_TODOWN("east_todown", DOWN, 0);

    public static final LogicFacing[] VALUES = LogicFacing.values();
    public static final Direction[] DIRECTIONS = Direction.values();

    private final String name;
    private final Direction inputSide;
    private final int rotationStep;

    LogicFacing(String name, Direction inputSide, int rotationStep) {
        this.name = name;
        this.inputSide = inputSide;
        this.rotationStep = rotationStep;
    }

    @Override
    public String getSerializedName() {
        return name;
    }


    public Direction getInputSide() {
        return inputSide;
    }

    public Direction getSide() {
        return DIRECTIONS[ordinal() / 4];
    }

    public int getRotationStep() {
        return rotationStep;
    }

    public static LogicFacing rotate(LogicFacing facing) {
        int current = facing.ordinal() & 3;
        current++;
        if (current > 3) {
            current = 0;
        }
        return LogicFacing.VALUES[(facing.ordinal() & ~3) + current];
    }


    @Override
    public String toString() {
        return name;
    }
}
