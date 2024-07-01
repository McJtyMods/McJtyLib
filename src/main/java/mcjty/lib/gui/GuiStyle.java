package mcjty.lib.gui;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum GuiStyle {
    STYLE_BEVEL("bevel"),
    STYLE_BEVEL_GRADIENT("bevel gradient"),
    STYLE_FLAT("flat"),
    STYLE_FLAT_GRADIENT("flat gradient"),
    STYLE_THICK("thick");

    private final String style;

    public static final StreamCodec<FriendlyByteBuf, GuiStyle> CODEC = NeoForgeStreamCodecs.enumCodec(GuiStyle.class);

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
