package mcjty.lib.varia;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.material.Fluid;

/** Mutable fluid amount in millibuckets. 1 mB equals 81 Fabric droplets. */
public final class FluidStack {

    public static final FluidStack EMPTY = new FluidStack(FluidVariant.blank(), 0);

    // Preserve the library's existing id/amount/components persistence format.
    public static final Codec<FluidStack> CODEC =
            RecordCodecBuilder.create(
                    instance ->
                            instance.group(
                                            BuiltInRegistries.FLUID
                                                    .byNameCodec()
                                                    .fieldOf("id")
                                                    .forGetter(FluidStack::getFluid),
                                            Codec.intRange(1, Integer.MAX_VALUE)
                                                    .fieldOf("amount")
                                                    .forGetter(FluidStack::getAmount),
                                            DataComponentPatch.CODEC
                                                    .optionalFieldOf(
                                                            "components", DataComponentPatch.EMPTY)
                                                    .forGetter(FluidStack::getComponentsPatch))
                                    .apply(
                                            instance,
                                            (fluid, amount, patch) ->
                                                    new FluidStack(
                                                            FluidVariant.of(fluid, patch),
                                                            amount)));

    public static final Codec<FluidStack> OPTIONAL_CODEC =
            ExtraCodecs.optionalEmptyMap(CODEC)
                    .xmap(
                            optional -> optional.orElse(EMPTY),
                            stack -> stack.isEmpty() ? Optional.empty() : Optional.of(stack));

    public Component getHoverName() {
        return FluidVariantAttributes.getName(variant());
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidStack> STREAM_CODEC =
            StreamCodec.of(
                    (b, s) -> {
                        FluidVariant.PACKET_CODEC.encode(b, s.variant);
                        b.writeVarInt(s.amount);
                    },
                    b -> new FluidStack(FluidVariant.PACKET_CODEC.decode(b), b.readVarInt()));

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidStack> OPTIONAL_STREAM_CODEC =
            STREAM_CODEC;

    private final FluidVariant variant;
    private int amount;

    public FluidStack(FluidVariant variant, int amount) {
        this.variant = variant;
        this.amount = Math.max(0, amount);
    }

    public FluidStack(Fluid fluid, int amount) {
        this(FluidVariant.of(fluid), amount);
    }

    public FluidStack(Holder<Fluid> fluid, int amount, DataComponentPatch patch) {
        this(FluidVariant.of(fluid.value(), patch), amount);
    }

    public FluidVariant variant() {
        return isEmpty() ? FluidVariant.blank() : variant;
    }

    public Fluid getFluid() {
        return variant.getFluid();
    }

    public int getAmount() {
        return amount;
    }

    public DataComponentPatch getComponentsPatch() {
        return variant.getComponents();
    }

    public boolean isEmpty() {
        return amount <= 0 || variant.isBlank();
    }

    public FluidStack copy() {
        return new FluidStack(variant, amount);
    }

    public FluidStack copyWithAmount(int n) {
        return new FluidStack(variant, n);
    }

    public void setAmount(int n) {
        if (this == EMPTY) throw new IllegalStateException("Cannot mutate EMPTY");
        amount = Math.max(0, n);
    }

    public void grow(int n) {
        setAmount(Math.addExact(amount, n));
    }

    public void shrink(int n) {
        setAmount(amount - n);
    }

    public Holder<Fluid> getFluidHolder() {
        return getFluid().builtInRegistryHolder();
    }

    public static Optional<FluidStack> parse(HolderLookup.Provider provider, CompoundTag tag) {
        return CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), tag).result();
    }

    public Tag save(HolderLookup.Provider provider, CompoundTag tag) {
        var encoded =
                CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), this)
                        .getOrThrow();
        tag.merge((CompoundTag) encoded);
        return tag;
    }

    public CompoundTag saveOptional(HolderLookup.Provider provider) {
        return isEmpty() ? new CompoundTag() : (CompoundTag) save(provider, new CompoundTag());
    }

    public static FluidStack parseOptional(HolderLookup.Provider provider, CompoundTag tag) {
        return parse(provider, tag).orElse(EMPTY);
    }

    public boolean isFluidEqual(FluidStack other) {
        return isSameFluidSameComponents(this, other);
    }

    public Component getDisplayName() {
        return getHoverName();
    }

    public static boolean isSameFluidSameComponents(FluidStack a, FluidStack b) {
        return a.variant.equals(b.variant);
    }

    public static boolean matches(FluidStack a, FluidStack b) {
        return a.isEmpty() && b.isEmpty()
                || a.amount == b.amount && isSameFluidSameComponents(a, b);
    }
}
