package mcjty.lib.gui;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import mcjty.lib.network.StreamCodecs;

public enum BuffStyle {
    OFF("off"),
    TOPLEFT("topleft"),
    TOPRIGHT("topright"),
    BOTLEFT("botleft"),
    BOTRIGHT("botright");

    private final String name;

    public static final Codec<BuffStyle> CODEC = Codec.STRING.xmap(BuffStyle::getStyle, BuffStyle::getName);
    public static final StreamCodec<FriendlyByteBuf, BuffStyle> STREAM_CODEC = StreamCodecs.enumCodec(BuffStyle.class);

    BuffStyle(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static BuffStyle getStyle(String name) {
        for (BuffStyle style : values()) {
            if (style.getName().equalsIgnoreCase(name)) {
                return style;
            }
        }
        return null;
    }
}
