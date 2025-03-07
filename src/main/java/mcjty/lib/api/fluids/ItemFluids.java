package mcjty.lib.api.fluids;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

// Used as a data component for items that can store fluids
public record ItemFluids(List<FluidStack> fluids) {
    public static final Codec<ItemFluids> ITEM_FLUIDS_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.list(FluidStack.OPTIONAL_CODEC).fieldOf("items").forGetter(ItemFluids::fluids)
            ).apply(instance, ItemFluids::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemFluids> ITEM_FLUIDS_STREAM_CODEC = StreamCodec.composite(
            FluidStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()), ItemFluids::fluids,
            ItemFluids::new);
}
