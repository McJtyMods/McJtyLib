package mcjty.lib.api.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

// Used as a data component for items that can store energy
public record ItemEnergy(int energy) {
    public static final Codec<ItemEnergy> ITEM_ENERGY_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("energy").forGetter(ItemEnergy::energy)
            ).apply(instance, ItemEnergy::new));
    public static final StreamCodec<ByteBuf, ItemEnergy> ITEM_ENERGY_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ItemEnergy::energy,
            ItemEnergy::new);
}
