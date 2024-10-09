package mcjty.lib.varia;

import com.mojang.serialization.Codec;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.HashMap;
import java.util.Map;

public enum RedstoneMode implements StringRepresentable {
    REDSTONE_IGNORED("Ignored"),
    REDSTONE_OFFREQUIRED("Off"),
    REDSTONE_ONREQUIRED("On");

    private static final Map<String,RedstoneMode> modeToMode = new HashMap<>();

    public static final Codec<RedstoneMode> CODEC = StringRepresentable.fromEnum(RedstoneMode::values);
    public static final StreamCodec<FriendlyByteBuf, RedstoneMode> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(RedstoneMode.class);

    private final String description;

    RedstoneMode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static RedstoneMode getMode(String mode) {
        return modeToMode.get(mode);
    }

    static {
        for (RedstoneMode mode : values()) {
            modeToMode.put(mode.description, mode);
        }
    }

    @Override
    public String getSerializedName() {
        return name();
    }
}
