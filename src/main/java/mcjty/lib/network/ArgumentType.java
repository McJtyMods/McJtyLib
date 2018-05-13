package mcjty.lib.network;

import java.util.HashMap;
import java.util.Map;

// @todo, remove this
public enum ArgumentType {
    TYPE_STRING(0),
    TYPE_INTEGER(1),
    TYPE_BLOCKPOS(2),
    TYPE_BOOLEAN(3),
    TYPE_DOUBLE(4),
    TYPE_STACK(5),
    TYPE_LONG(6),
    TYPE_STRING_LIST(7);

    private final int index;
    private static final Map<Integer, ArgumentType> mapping = new HashMap<>();

    static {
        for (ArgumentType type : values()) {
            mapping.put(type.index, type);
        }
    }

    ArgumentType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static ArgumentType getType(int index) {
        return mapping.get(index);
    }
}
