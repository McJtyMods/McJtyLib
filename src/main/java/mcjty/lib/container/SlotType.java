package mcjty.lib.container;

import java.util.HashMap;
import java.util.Map;

public enum SlotType {
    SLOT_UNKNOWN("unknown"),
    SLOT_GHOST("ghost"),               // Ghost slot as used by crafting grids
    SLOT_GHOSTOUT("ghostout"),         // Ghost slot for crafting output
    SLOT_INPUT("input"),               // Inventory slot that can accept items in sided inventories
    SLOT_OUTPUT("output"),             // Inventory slot that can output items in sided inventories
    SLOT_CONTAINER("container"),       // Inventory slot that cannot accept nor output items in sided inventories
    SLOT_SPECIFICITEM("specificitem"), // Only a specific item fits in this slot
    SLOT_PLAYERINV("playerinv"),       // Player inventory slot
    SLOT_PLAYERHOTBAR("playerhotbar"), // Player hotbar slot
    SLOT_CRAFTRESULT("craftresult");   // Crafting result

    private final String name;

    private static final Map<String, SlotType> SLOT_TYPE_MAP = new HashMap<>();

    static {
        for (SlotType type : SlotType.values()) {
            SLOT_TYPE_MAP.put(type.getName(), type);
        }
    }

    SlotType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SlotType findByName(String name) {
        return SLOT_TYPE_MAP.get(name);
    }
}
