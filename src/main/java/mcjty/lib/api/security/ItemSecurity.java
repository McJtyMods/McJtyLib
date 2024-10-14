package mcjty.lib.api.security;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ItemSecurity(String owner, int channel) {
    public static final Codec<ItemSecurity> ITEM_SECURITY_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("ownerUUID").forGetter(ItemSecurity::owner),
                    Codec.INT.fieldOf("channel").forGetter(ItemSecurity::channel)
            ).apply(instance, ItemSecurity::new));
    public static final StreamCodec<ByteBuf, ItemSecurity> ITEM_SECURITY_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ItemSecurity::owner,
            ByteBufCodecs.INT, ItemSecurity::channel,
            ItemSecurity::new);
}
