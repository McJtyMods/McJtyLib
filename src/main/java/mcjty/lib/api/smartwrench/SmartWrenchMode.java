package mcjty.lib.api.smartwrench;

import java.util.HashMap;
import java.util.Map;

public enum SmartWrenchMode {
    MODE_WRENCH("w", "wrench"),
    MODE_SELECT("s", "focus");

    private static final Map<String,SmartWrenchMode> CODE_TO_MODE = new HashMap<>();

    private final String code;
    private final String name;

    static {
        for (SmartWrenchMode mode : values()) {
            CODE_TO_MODE.put(mode.getCode(), mode);
        }
    }

    SmartWrenchMode(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static SmartWrenchMode getMode(String code) {
        return CODE_TO_MODE.get(code);
    }
}
