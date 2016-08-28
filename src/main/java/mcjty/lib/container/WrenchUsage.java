package mcjty.lib.container;

public enum WrenchUsage {
    NOT,                // Not a wrench
    NORMAL,             // Normal wrench usage
    SNEAKING,           // Sneaking mode with wrench
    DISABLED,           // It is a wrench but it is disabled
    SELECT,             // In focus mode (smart wrench only)
    SNEAK_SELECT,       // Sneak focus mode (smart wrench only)
}
