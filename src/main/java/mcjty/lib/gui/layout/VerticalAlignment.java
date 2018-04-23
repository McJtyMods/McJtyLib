package mcjty.lib.gui.layout;

public enum VerticalAlignment {
    ALIGN_TOP,
    ALIGN_BOTTOM,
    ALIGN_CENTER;


    public static VerticalAlignment getByName(String name) {
        for (VerticalAlignment alignment : values()) {
            if (name.equals(alignment.name())) {
                return alignment;
            }
        }
        return null;
    }

}
