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
    EAST_DN,

    DOWN_NW_BACK,
    DOWN_NE_BACK,
    DOWN_SW_BACK,
    DOWN_SE_BACK,

    UP_NW_BACK,
    UP_NE_BACK,
    UP_SW_BACK,
    UP_SE_BACK,

    NORTH_UW_BACK,
    NORTH_UE_BACK,
    NORTH_DW_BACK,
    NORTH_DE_BACK,

    SOUTH_UW_BACK,
    SOUTH_UE_BACK,
    SOUTH_DW_BACK,
    SOUTH_DE_BACK,

    WEST_US_BACK,
    WEST_UN_BACK,
    WEST_DS_BACK,
    WEST_DN_BACK,

    EAST_US_BACK,
    EAST_UN_BACK,
    EAST_DS_BACK,
    EAST_DN_BACK;


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
