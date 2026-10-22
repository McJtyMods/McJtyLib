package mcjty.lib.varia;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.material.Fluid;

/** Mutable fluid amount in millibuckets. 1 mB equals 81 Fabric droplets. */
public final class FluidStack {

    public static final FluidStack EMPTY=new FluidStack(FluidVariant.blank(),0);

    // Preserve the library's existing id/amount/components persistence format.
    public static final Codec<FluidStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            net.minecraft.core.registries.BuiltInRegistries.FLUID.byNameCodec().fieldOf("id").forGetter(FluidStack::getFluid),
            Codec.intRange(1, Integer.MAX_VALUE).fieldOf("amount").forGetter(FluidStack::getAmount),
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(FluidStack::getComponentsPatch)
    ).apply(instance, (fluid, amount, patch) -> new FluidStack(FluidVariant.of(fluid, patch), amount)));

    public static final Codec<FluidStack> OPTIONAL_CODEC = net.minecraft.util.ExtraCodecs.optionalEmptyMap(CODEC)
            .xmap(optional -> optional.orElse(EMPTY), stack -> stack.isEmpty() ? java.util.Optional.empty() : java.util.Optional.of(stack));

    public net.minecraft.network.chat.Component getHoverName() {
        return net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes.getName(variant());
    }

    public static final StreamCodec<RegistryFriendlyByteBuf,FluidStack> STREAM_CODEC=StreamCodec.of((b,s)-> {
        FluidVariant.PACKET_CODEC.encode(b,s.variant);b.writeVarInt(s.amount);
    },b->new FluidStack(FluidVariant.PACKET_CODEC.decode(b),b.readVarInt()));

    public static final StreamCodec<RegistryFriendlyByteBuf,FluidStack> OPTIONAL_STREAM_CODEC=STREAM_CODEC;

    private final FluidVariant variant;
    private int amount;

    public FluidStack(FluidVariant variant,int amount) {
        this.variant=variant;
        this.amount=Math.max(0,amount);
    }

    public FluidStack(Fluid fluid,int amount) {
        this(FluidVariant.of(fluid),amount);
    }

    public FluidStack(Holder<Fluid> fluid,int amount,DataComponentPatch patch) {
        this(FluidVariant.of(fluid.value(),patch),amount);
    }

    public FluidVariant variant() {
        return isEmpty()?FluidVariant.blank():variant;
    }

    public Fluid getFluid() {
        return variant.getFluid();
    }

    public int getAmount() {
        return amount;
    }

    public DataComponentPatch getComponentsPatch() {
        return variant.getComponentsPatch();
    }

    public boolean isEmpty() {
        return amount<=0||variant.isBlank();
    }

    public FluidStack copy() {
        return new FluidStack(variant,amount);
    }

    public FluidStack copyWithAmount(int n) {
        return new FluidStack(variant,n);
    }

    public void setAmount(int n) {
        if(this==EMPTY)throw new IllegalStateException("Cannot mutate EMPTY");
        amount=Math.max(0,n);
    }

    public void grow(int n) {
        setAmount(Math.addExact(amount,n));
    }
    public void shrink(int n) {
        setAmount(amount-n);
    }

    public static boolean isSameFluidSameComponents(FluidStack a,FluidStack b) {
        return a.variant.equals(b.variant);
    }

    public static boolean matches(FluidStack a,FluidStack b) {
        return a.isEmpty()&&b.isEmpty()||a.amount==b.amount&&isSameFluidSameComponents(a,b);
    }

}
