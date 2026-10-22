package mcjty.lib.varia;

import mcjty.lib.api.fluids.ItemFluids;
import mcjty.lib.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import mcjty.lib.varia.FluidStack;
import mcjty.lib.varia.IFluidTank;
import mcjty.lib.varia.IFluidHandler;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;


import mcjty.lib.container.IndexedStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import team.reborn.energy.api.EnergyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class CustomTank implements IFluidHandler, IFluidTank, IndexedStorage<FluidVariant> {

    @Nonnull
    protected FluidStack fluid = FluidStack.EMPTY;
    protected int capacity;

    private final SnapshotParticipant<FluidStack> transferJournal = new SnapshotParticipant<>() {
        private FluidStack before;
        @Override protected void releaseSnapshot(FluidStack snapshot) { before=snapshot; }
        @Override protected FluidStack createSnapshot() { return fluid.copy(); }
        @Override protected void readSnapshot(FluidStack snapshot) { fluid = snapshot; }
        @Override protected void onFinalCommit() { if(!FluidStack.matches(before,fluid)) onContentsChanged(); }
    };
    private void checkIndex(int index) { if (index != 0) throw new IndexOutOfBoundsException(index); }
    @Override public int size() { return 1; }
    @Override public FluidVariant getResource(int index) { checkIndex(index); return fluid.variant(); }
    @Override public long getAmountAsLong(int index) { checkIndex(index); return fluid.getAmount() * 81L; }
    @Override public long getCapacityAsLong(int index, FluidVariant resource) { checkIndex(index); return capacity * 81L; }
    @Override public boolean isValid(int index, FluidVariant resource) { checkIndex(index); return !resource.isBlank() && isFluidValid(new FluidStack(resource, 1)); }
    @Override public long insert(int index, FluidVariant resource, long amount, TransactionContext transaction) {
        checkIndex(index);
        if (amount < 0) throw new IllegalArgumentException("Negative amount");
        if (amount == 0 || resource.isBlank()) return 0;
        int inserted = fill(new FluidStack(resource, (int)Math.min(amount/81,Integer.MAX_VALUE)), FluidAction.SIMULATE);
        if (inserted > 0) {
            transferJournal.updateSnapshots(transaction);
            fluid = new FluidStack(resource, fluid.getAmount()+inserted);
        }
        return inserted * 81L;
    }
    @Override public long extract(int index, FluidVariant resource, long amount, TransactionContext transaction) {
        checkIndex(index);
        if (amount < 0) throw new IllegalArgumentException("Negative amount");
        if (amount == 0 || resource.isBlank() || !resource.equals(fluid.variant())) return 0;
        int extracted = (int)Math.min(amount/81, fluid.getAmount());
        if (extracted > 0) {
            transferJournal.updateSnapshots(transaction);
            fluid = fluid.copyWithAmount(fluid.getAmount() - extracted);
        }
        return extracted * 81L;
    }

    public CustomTank(int capacity) {
        this.capacity = capacity;
    }

    public CustomTank setCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return true;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    @Nonnull
    public FluidStack getFluid() {
        return fluid;
    }

    @Override
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
        builder.set(Registration.ITEM_FLUIDS.get(), new ItemFluids(fluidStacks));
    }


    @Override
    public int getTanks() {
        return 1;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !isFluidValid(resource)) {
            return 0;
        }
        if (action.simulate()) {
            if (fluid.isEmpty()) {
                return Math.min(capacity, resource.getAmount());
            }
            if (!FluidStack.isSameFluidSameComponents(fluid, resource)) {
                return 0;
            }
            return Math.min(capacity - fluid.getAmount(), resource.getAmount());
        }
        if (fluid.isEmpty()) {
            onContentsChanged();
            fluid = new FluidStack(resource.getFluid().builtInRegistryHolder(), Math.min(capacity, resource.getAmount()), resource.getComponentsPatch());
            return fluid.getAmount();
        }
        if (!FluidStack.isSameFluidSameComponents(fluid, resource)) {
            return 0;
        }
        int filled = capacity - fluid.getAmount();

        if (resource.getAmount() < filled) {
            onContentsChanged();
            fluid.grow(resource.getAmount());
            filled = resource.getAmount();
        } else {
            onContentsChanged();
            fluid.setAmount(capacity);
        }
        return filled;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !FluidStack.isSameFluidSameComponents(resource, fluid)) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        int drained = maxDrain;
        if (fluid.getAmount() < drained) {
            drained = fluid.getAmount();
        }
        FluidStack stack = new FluidStack(fluid.getFluid().builtInRegistryHolder(), drained, fluid.getComponentsPatch());
        if (action.execute()) {
            onContentsChanged();
            fluid.shrink(drained);
        }
        if (action.execute() && fluid.getAmount() <= 0) {
            fluid = FluidStack.EMPTY;
        }
        return stack;
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
