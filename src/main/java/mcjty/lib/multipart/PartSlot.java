package mcjty.lib.multipart;

import java.util.HashMap;
import java.util.Map;

public enum PartSlot {
    NONE,

    DOWN,
    UP,
    NORTH,
    SOUTH,
    WEST,
    EAST,

    DOWN_NW,
    DOWN_NE,
    DOWN_SW,
    DOWN_SE,

    UP_NW,
    UP_NE,
    UP_SW,
    UP_SE,

    NORTH_UW,
    NORTH_UE,
    NORTH_DW,
    NORTH_DE,

    SOUTH_UW,
    SOUTH_UE,
    SOUTH_DW,
    SOUTH_DE,

    WEST_US,
    WEST_UN,
    WEST_DS,
    WEST_DN,

    EAST_US,
    EAST_UN,
    EAST_DS,
    EAST_DN;

    public static final PartSlot[] VALUES = PartSlot.values();
    private static final Map<String, PartSlot> SLOT_MAP = new HashMap<>();

    static {
        for (PartSlot value : values()) {
            SLOT_MAP.put(value.name(), value);
        }
    }

    public static PartSlot byName(String name) {
        return SLOT_MAP.get(name);
    }
}
