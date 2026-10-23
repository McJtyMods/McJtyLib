package mcjty.lib.varia;

import mcjty.lib.api.fluids.ItemFluids;
import mcjty.lib.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;


import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class CustomTank implements ResourceHandler<FluidResource> {

    @Nonnull
    protected FluidStack fluid = FluidStack.EMPTY;
    protected int capacity;

    private final SnapshotJournal<FluidStack> transferJournal = new SnapshotJournal<>() {
        @Override protected FluidStack createSnapshot() { return fluid.copy(); }
        @Override protected void revertToSnapshot(FluidStack snapshot) { fluid = snapshot; }
        @Override protected void onRootCommit(FluidStack before) {
            if (!FluidStack.matches(before, fluid)) onContentsChanged();
        }
    };
    private void checkIndex(int index) { if (index != 0) throw new IndexOutOfBoundsException(index); }
    @Override public int size() { return 1; }
    @Override public FluidResource getResource(int index) { checkIndex(index); return FluidResource.of(fluid); }
    @Override public long getAmountAsLong(int index) { checkIndex(index); return fluid.getAmount(); }
    @Override public long getCapacityAsLong(int index, FluidResource resource) { checkIndex(index); return capacity; }
    @Override public boolean isValid(int index, FluidResource resource) { checkIndex(index); return !resource.isEmpty() && isFluidValid(resource.toStack(1)); }
    @Override public int insert(int index, FluidResource resource, int amount, TransactionContext transaction) {
        checkIndex(index);
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        if (amount == 0) return 0;
        if (!isValid(index, resource) || (!fluid.isEmpty() && !resource.matches(fluid))) return 0;
        int inserted = Math.min(amount, Math.max(0, capacity - fluid.getAmount()));
        if (inserted > 0) {
            transferJournal.updateSnapshots(transaction);
            fluid = resource.toStack(fluid.getAmount() + inserted);
        }
        return inserted;
    }
    @Override public int extract(int index, FluidResource resource, int amount, TransactionContext transaction) {
        checkIndex(index);
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        if (amount == 0 || resource.isEmpty() || !resource.matches(fluid)) return 0;
        int extracted = Math.min(amount, fluid.getAmount());
        if (extracted > 0) {
            transferJournal.updateSnapshots(transaction);
            fluid = fluid.copyWithAmount(fluid.getAmount() - extracted);
        }
        return extracted;
    }

    public CustomTank(int capacity) {
        if (capacity < 0) throw new IllegalArgumentException("Negative capacity");
        this.capacity = capacity;
    }

    public CustomTank setCapacity(int capacity) {
        if (capacity < 0) throw new IllegalArgumentException("Negative capacity");
        this.capacity = capacity;
        return this;
    }

    public boolean isFluidValid(FluidStack stack) {
        return true;
    }

    public int getCapacity() {
        return capacity;
    }

    @Nonnull
    public FluidStack getFluid() {
        return fluid;
    }

    public int getFluidAmount() {
        return fluid.getAmount();
    }

    public void load(CompoundTag tag, String tagName, HolderLookup.Provider provider) {
        if (tag.contains(tagName)) {
            CompoundTag nbt = tag.getCompoundOrEmpty(tagName);
            FluidStack fluid = FluidStack.CODEC.parse(provider.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), nbt).result().orElse(FluidStack.EMPTY);
            setFluid(fluid);
        }
    }

    public void save(CompoundTag tag, String tagName, HolderLookup.Provider provider) {
        if (!fluid.isEmpty()) {
            CompoundTag nbt = new CompoundTag();
            Tag saved = FluidStack.CODEC.encodeStart(provider.createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE), fluid).getOrThrow();
            tag.put(tagName, saved);
        }
    }

    public void applyImplicitComponents(ItemFluids itemFluids) {
        if (itemFluids != null && !itemFluids.fluids().isEmpty()) {
            fluid = itemFluids.fluids().get(0);
        }
    }

    public void collectImplicitComponents(DataComponentMap.Builder builder) {
        List<FluidStack> fluidStacks = new ArrayList<>();
        fluidStacks.add(fluid);
        builder.set(Registration.ITEM_FLUIDS, new ItemFluids(fluidStacks));
    }


    public int getTanks() {
        return 1;
    }

    @Nonnull
    public FluidStack getFluidInTank(int tank) {
        return getFluid();
    }

    public int getTankCapacity(int tank) {
        return getCapacity();
    }

    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return isFluidValid(stack);
    }

    /** Fill immediately, or roll back the transaction when simulating. */
    public int fill(FluidStack resource, boolean simulate) {
        if (resource.isEmpty()) return 0;
        try (var transaction = Transaction.openRoot()) {
            int inserted = insert(0, FluidResource.of(resource), resource.getAmount(), transaction);
            if (!simulate) transaction.commit();
            return inserted;
        }
    }

    @Nonnull
    public FluidStack drain(FluidStack resource, boolean simulate) {
        if (resource.isEmpty() || !FluidStack.isSameFluidSameComponents(resource, fluid)) return FluidStack.EMPTY;
        return drain(resource.getAmount(), simulate);
    }

    @Nonnull
    public FluidStack drain(int amount, boolean simulate) {
        if (fluid.isEmpty()) return FluidStack.EMPTY;
        var resource = FluidResource.of(fluid);
        try (var transaction = Transaction.openRoot()) {
            int extracted = extract(0, resource, amount, transaction);
            if (!simulate) transaction.commit();
            return resource.toStack(extracted);
        }
    }

    protected void onContentsChanged() {

    }

    public void setFluid(@Nonnull FluidStack stack) {
        this.fluid = stack;
    }

    public boolean isEmpty() {
        return fluid.isEmpty();
    }

    public int getSpace() {
        return Math.max(0, capacity - fluid.getAmount());
    }

}
