package mcjty.lib.gui;

public enum GuiStyle {
    STYLE_BEVEL("bevel"),
    STYLE_BEVEL_GRADIENT("bevel gradient"),
    STYLE_FLAT("flat"),
    STYLE_FLAT_GRADIENT("flat gradient"),
    STYLE_THICK("thick");

    private final String style;

    GuiStyle(String style) {
        this.style = style;
    }

    public String getStyle() {
        return style;
    }

    public static GuiStyle getStyle(String name) {
        for (GuiStyle style : values()) {
            if (style.getStyle().equals(name)) {
                return style;
            }
        }
        return null;
    }
}
