package mcjty.lib.api.infusable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

// Used as a data component for items that can be infused
public record ItemInfusable(int infused) {
    public static final Codec<ItemInfusable> ITEM_INFUSABLE_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("infused").forGetter(ItemInfusable::infused)
            ).apply(instance, ItemInfusable::new));
    public static final StreamCodec<ByteBuf, ItemInfusable> ITEM_INFUSABLE_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ItemInfusable::infused,
            ItemInfusable::new);
}
