package mcjty.lib.gui.layout;

public enum HorizontalAlignment {
    ALIGN_LEFT,
    ALIGN_RIGHT,
    ALIGN_CENTER;

    public static HorizontalAlignment getByName(String name) {
        for (HorizontalAlignment alignment : values()) {
            if (name.equals(alignment.name())) {
                return alignment;
            }
        }
        return null;
    }
}
