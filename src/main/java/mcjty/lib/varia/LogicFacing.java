package mcjty.lib.varia;

import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;

import static net.minecraft.util.Direction.*;

public enum LogicFacing implements IStringSerializable {
    DOWN_TONORTH("down_tonorth", NORTH),
    DOWN_TOSOUTH("down_tosouth", SOUTH),
    DOWN_TOWEST("down_towest", WEST),
    DOWN_TOEAST("down_toeast", EAST),

    UP_TONORTH("up_tonorth", NORTH),
    UP_TOSOUTH("up_tosouth", SOUTH),
    UP_TOWEST("up_towest", WEST),
    UP_TOEAST("up_toeast", EAST),

    NORTH_TOWEST("north_towest", WEST),
    NORTH_TOEAST("north_toeast", EAST),
    NORTH_TOUP("north_toup", UP),
    NORTH_TODOWN("north_todown", DOWN),

    SOUTH_TOWEST("south_towest", WEST),
    SOUTH_TOEAST("south_toeast", EAST),
    SOUTH_TOUP("south_toup", UP),
    SOUTH_TODOWN("south_todown", DOWN),

    WEST_TONORTH("west_tonorth", NORTH),
    WEST_TOSOUTH("west_tosouth", SOUTH),
    WEST_TOUP("west_toup", UP),
    WEST_TODOWN("west_todown", DOWN),

    EAST_TONORTH("east_tonorth", NORTH),
    EAST_TOSOUTH("east_tosouth", SOUTH),
    EAST_TOUP("east_toup", UP),
    EAST_TODOWN("east_todown", DOWN);

    public static final LogicFacing[] VALUES = LogicFacing.values();
    public static final Direction[] DIRECTIONS = Direction.values();

    private final String name;
    private final Direction inputSide;

    LogicFacing(String name, Direction inputSide) {
        this.name = name;
        this.inputSide = inputSide;
    }

    @Override
    public String getName() {
        return name;
    }

    public Direction getInputSide() {
        return inputSide;
    }

    public Direction getSide() {
        return DIRECTIONS[ordinal() / 4];
    }

    public int getRotationStep() {
        return ordinal() & 3;
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
