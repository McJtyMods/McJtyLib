package mcjty.lib.container;

import mcjty.lib.api.container.ItemInventory;
import mcjty.lib.setup.Registration;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;


import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class GenericItemHandler implements IItemHandlerModifiable, ResourceHandler<ItemResource> {

    private final GenericTileEntity tileEntity;
    private final ContainerFactory containerFactory;
    private final ItemStackHandler stacks;

    private final SnapshotJournal<java.util.List<ItemStack>> transferJournal = new SnapshotJournal<>() {
        @Override protected java.util.List<ItemStack> createSnapshot() {
            var result = new java.util.ArrayList<ItemStack>();
            for (int i = 0; i < getSlots(); i++) result.add(getStackInSlot(i).copy());
            return result;
        }
        @Override protected void revertToSnapshot(java.util.List<ItemStack> snapshot) {
            for (int i = 0; i < snapshot.size(); i++) stacks.setStackInSlot(i, snapshot.get(i));
        }
        @Override protected void onRootCommit(java.util.List<ItemStack> before) {
            for (int i = 0; i < before.size(); i++) {
                if (!ItemStack.matches(before.get(i), getStackInSlot(i))) onUpdate(i, getStackInSlot(i));
            }
        }
    };
    @Override public int size() { return getSlots(); }
    @Override public ItemResource getResource(int index) { return ItemResource.of(getStackInSlot(index)); }
    @Override public long getAmountAsLong(int index) { return getStackInSlot(index).getCount(); }
    @Override public long getCapacityAsLong(int index, ItemResource resource) {
        if (containerFactory.isGhostSlot(index) || containerFactory.isGhostOutputSlot(index)) return 0;
        return Math.min(getSlotLimit(index), resource.isEmpty() ? 64 : resource.getMaxStackSize());
    }
    @Override public boolean isValid(int index, ItemResource resource) { return !resource.isEmpty() && isItemValid(index, resource.toStack()); }
    @Override public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (amount < 0) throw new IllegalArgumentException("Negative amount");
        if (amount == 0 || resource.isEmpty() || getCapacityAsLong(index, resource) == 0) return 0;
        int inserted = amount - insertItem(index, resource.toStack(amount), true).getCount();
        if (inserted > 0) {
            transferJournal.updateSnapshots(transaction);
            stacks.setStackInSlot(index, resource.toStack(getStackInSlot(index).getCount() + inserted));
        }
        return inserted;
    }
    @Override public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        if (amount < 0) throw new IllegalArgumentException("Negative amount");
        if (amount == 0 || resource.isEmpty() || !resource.matches(getStackInSlot(index)) || getCapacityAsLong(index, resource) == 0) return 0;
        int extracted = extractItem(index, amount, true).getCount();
        if (extracted > 0) {
            transferJournal.updateSnapshots(transaction);
            stacks.setStackInSlot(index, getStackInSlot(index).copyWithCount(getStackInSlot(index).getCount() - extracted));
        }
        return extracted;
    }

    public static BiPredicate<Integer, ItemStack> slot(int s) {
        return (slot, stack) -> slot == s;
    }

    public static BiPredicate<Integer, ItemStack> notslot(int s) {
        return (slot, stack) -> slot != s;
    }

    public static BiPredicate<Integer, ItemStack> match(Supplier<? extends Item> itemSupplier) {
        Item item = itemSupplier.get();
        return (slot, stack) -> stack.getItem() == item;
    }

    public static BiPredicate<Integer, ItemStack> match(Item item) {
        return (slot, stack) -> stack.getItem() == item;
    }

    public static BiPredicate<Integer, ItemStack> no() {
        return (slot, stack) -> false;
    }

    public static BiPredicate<Integer, ItemStack> yes() {
        return (slot, stack) -> true;
    }

    /**
     * Make a builder for a GenericItemHandler
     */
    public static GenericItemHandler.Builder create(GenericTileEntity te, Supplier<ContainerFactory> factorySupplier) {
        return new Builder(te, factorySupplier);
    }

    /**
     * Conveniance method to create a basic item handler with no restrictions
     */
    public static GenericItemHandler basic(GenericTileEntity te, Supplier<ContainerFactory> factorySupplier) {
        return new GenericItemHandler(te, factorySupplier.get());
    }

    // Called when something changed
    protected void onUpdate(int index, ItemStack stack) {
        tileEntity.markDirtyQuick();
    }

    public GenericItemHandler(GenericTileEntity te, ContainerFactory factory) {
        this.tileEntity = te;
        this.containerFactory = factory;
        stacks = new ItemStackHandler(containerFactory.getContainerSlots());
    }

    public void applyImplicitComponents(ItemInventory inventory) {
        if (inventory != null) {
            setStacks(inventory.items());
        }
    }

    public void collectImplicitComponents(DataComponentMap.Builder builder) {
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < getSlots(); i++) {
            stacks.add(getStackInSlot(i));
        }
        builder.set(Registration.ITEM_INVENTORY, new ItemInventory(stacks));
    }

    public ItemStackHandler getStacks() {
        return stacks;
    }

    public void setStacks(List<ItemStack> items) {
        for (int i = 0; i < stacks.getSlots(); i++) {
            if (i < items.size()) {
                stacks.setStackInSlot(i, items.get(i));
            } else {
                stacks.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }


    @Override
    public int getSlots() {
        return stacks.getSlots();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot >= stacks.getSlots()) {
            return ItemStack.EMPTY;
        }
        return stacks.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (!isItemInsertable(slot, stack)) {
            return stack;
        }

        ItemStack stackInSlot = getStackInSlot(slot);

        int m;
        if (!stackInSlot.isEmpty()) {
            if (stackInSlot.getCount() >= Math.min(stackInSlot.getMaxStackSize(), getSlotLimit(slot))) {
                return stack;
            }

            if (!ItemStack.isSameItemSameComponents(stack, stackInSlot)) {
                return stack;
            }

            if (!isItemValid(slot, stack)) {
                return stack;
            }

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - stackInSlot.getCount();

            if (stack.getCount() <= m) {
                if (!simulate) {
                    ItemStack copy = stack.copy();
                    copy.grow(stackInSlot.getCount());
                    setInventorySlotContents(copy.getMaxStackSize(), slot, copy);
                    onUpdate(slot, copy);
                }

                return ItemStack.EMPTY;
            } else {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    ItemStack copy = stack.split(m);
                    copy.grow(stackInSlot.getCount());
                    setInventorySlotContents(copy.getMaxStackSize(), slot, copy);
                    onUpdate(slot, copy);
                    return stack;
                } else {
                    stack.shrink(m);
                    return stack;
                }
            }
        } else {
            if (!isItemValid(slot, stack)) {
                return stack;
            }

            m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
            if (m < stack.getCount()) {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    ItemStack split = stack.split(m);
                    setInventorySlotContents(stack.getMaxStackSize(), slot, split);
                    onUpdate(slot, split);
                    return stack;
                } else {
                    stack.shrink(m);
                    return stack;
                }
            } else {
                if (!simulate) {
                    setInventorySlotContents(stack.getMaxStackSize(), slot, stack);
                    onUpdate(slot, stack);
                }
                return ItemStack.EMPTY;
            }
        }

    }

    public void setInventorySlotContents(int stackLimit, int index, ItemStack stack) {
        if (index >= stacks.getSlots()) {
            return;
        }

        if (containerFactory.isGhostSlot(index)) {
            if (!stack.isEmpty()) {
                ItemStack stack1 = stack.copy();
                if (index < 9) {
                    stack1.setCount(1);
                }
                stacks.setStackInSlot(index, stack1);
            } else {
                stacks.setStackInSlot(index, ItemStack.EMPTY);
            }
        } else if (containerFactory.isGhostOutputSlot(index)) {
            if (!stack.isEmpty()) {
                stacks.setStackInSlot(index, stack.copy());
            } else {
                stacks.setStackInSlot(index, ItemStack.EMPTY);
            }
        } else {
            stacks.setStackInSlot(index, stack);
            if (!stack.isEmpty() && stack.getCount() > stackLimit) {
                stack.setCount(Math.max(stackLimit, 0));
            }
            tileEntity.setChanged();
        }
    }


    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = getStackInSlot(slot);

        if (stackInSlot.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (!isItemExtractable(slot, stackInSlot)) {
            return ItemStack.EMPTY;
        }

        if (simulate) {
            if (stackInSlot.getCount() < amount) {
                return stackInSlot.copy();
            } else {
                ItemStack copy = stackInSlot.copy();
                copy.setCount(amount);
                return copy;
            }
        } else {
            int m = Math.min(stackInSlot.getCount(), amount);

            ItemStack decrStackSize = decrStackSize(slot, m);
            onUpdate(slot, decrStackSize);
            return decrStackSize;
        }
    }

    public ItemStack decrStackSize(int index, int amount) {
        if (index >= stacks.getSlots()) {
            return ItemStack.EMPTY;
        }

        if (containerFactory.isGhostSlot(index) || containerFactory.isGhostOutputSlot(index)) {
            ItemStack old = stacks.getStackInSlot(index);
            stacks.setStackInSlot(index, ItemStack.EMPTY);
            if (old.isEmpty()) {
                return ItemStack.EMPTY;
            }
            old.setCount(0);
            return old;
        } else {
            if (!stacks.getStackInSlot(index).isEmpty()) {
                if (stacks.getStackInSlot(index).getCount() <= amount) {
                    ItemStack old = stacks.getStackInSlot(index);
                    stacks.setStackInSlot(index, ItemStack.EMPTY);
                    tileEntity.setChanged();
                    return old;
                }
                ItemStack its = stacks.getStackInSlot(index).split(amount);
                if (stacks.getStackInSlot(index).isEmpty()) {
                    stacks.setStackInSlot(index, ItemStack.EMPTY);
                }
                tileEntity.setChanged();
                return its;
            }
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        stacks.setStackInSlot(slot, stack);
        onUpdate(slot, stack);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return true;
    }

    public boolean isItemInsertable(int slot, @Nonnull ItemStack stack) {
        return isItemValid(slot, stack);
    }

    public boolean isItemExtractable(int slot, @Nonnull ItemStack stack) {
        return true;
    }

    public ContainerFactory getContainerFactory() {
        return containerFactory;
    }

    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        var output = net.minecraft.world.level.storage.TagValueOutput.createWithContext(net.minecraft.util.ProblemReporter.DISCARDING, provider);
        stacks.serialize(output);
        return output.buildResult();
    }

    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        stacks.deserialize(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING, provider, nbt));
    }

    public void save(CompoundTag tag, String tagName, HolderLookup.Provider provider) {
        tag.put(tagName, serializeNBT(provider));
    }

    public void load(CompoundTag tag, String tagName, HolderLookup.Provider provider) {
        if (tag.contains(tagName)) {
            deserializeNBT(provider, tag.getCompoundOrEmpty(tagName));
        }
    }

    public static class Builder {
        private final GenericTileEntity te;
        private final Supplier<ContainerFactory> factorySupplier;
        private BiPredicate<Integer, ItemStack> itemValid = (slot, stack) -> true;
        private BiPredicate<Integer, ItemStack> insertable = null;
        private BiPredicate<Integer, ItemStack> extractable = (slot, stack) -> true;
        private BiConsumer<Integer, ItemStack> onUpdate = (slot, stack) -> {};
        private Function<Integer, Integer> slotLimit = s -> 64;

        public Builder(GenericTileEntity te, Supplier<ContainerFactory> factorySupplier) {
            this.te = te;
            this.factorySupplier = factorySupplier;
        }

        public Builder slotLimit(int slotLimit) {
            this.slotLimit = s -> slotLimit;
            return this;
        }

        public Builder slotLimit(Function<Integer, Integer> slotLimit) {
            this.slotLimit = slotLimit;
            return this;
        }

        public Builder itemValid(BiPredicate<Integer, ItemStack> itemValid) {
            this.itemValid = itemValid;
            return this;
        }

        public Builder insertable(BiPredicate<Integer, ItemStack> insertable) {
            this.insertable = insertable;
            return this;
        }

        public Builder extractable(BiPredicate<Integer, ItemStack> extractable) {
            this.extractable = extractable;
            return this;
        }

        public Builder onUpdate(BiConsumer<Integer, ItemStack> onUpdate) {
            this.onUpdate = onUpdate;
            return this;
        }

        public GenericItemHandler build() {
            return new GenericItemHandler(te, factorySupplier.get()) {

                @Override
                public int getSlotLimit(int slot) {
                    return slotLimit.apply(slot);
                }

                @Override
                public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                    return itemValid.test(slot, stack);
                }

                @Override
                public boolean isItemInsertable(int slot, @Nonnull ItemStack stack) {
                    if (insertable == null) {
                        return isItemValid(slot, stack);
                    } else {
                        return insertable.test(slot, stack);
                    }
                }

                @Override
                public boolean isItemExtractable(int slot, @Nonnull ItemStack stack) {
                    return extractable.test(slot, stack);
                }

                @Override
                protected void onUpdate(int index, ItemStack stack) {
                    super.onUpdate(index, stack);
                    onUpdate.accept(index, stack);
                }
            };
        }
    }
}
