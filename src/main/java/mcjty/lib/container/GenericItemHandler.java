package mcjty.lib.container;

import mcjty.lib.api.container.ItemInventory;
import mcjty.lib.setup.Registration;
import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.TransferPreconditions;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

public class GenericItemHandler implements ResourceHandler<ItemResource>, IndexModifier<ItemResource> {
    private final GenericTileEntity tileEntity;
    private final ContainerFactory containerFactory;
    private final ItemStacksResourceHandler stacks;
    private final SnapshotJournal<List<ItemStack>> transferJournal = new SnapshotJournal<>() {
        @Override protected List<ItemStack> createSnapshot() {
            var snapshot = new ArrayList<ItemStack>();
            for (int i = 0; i < size(); i++) snapshot.add(getStackInSlot(i));
            return snapshot;
        }
        @Override protected void revertToSnapshot(List<ItemStack> snapshot) {
            for (int i = 0; i < snapshot.size(); i++) setStoredStack(i, snapshot.get(i));
        }
        @Override protected void onRootCommit(List<ItemStack> before) {
            for (int i = 0; i < before.size(); i++) {
                if (!ItemStack.matches(before.get(i), getStackInSlot(i))) onUpdate(i, getStackInSlot(i));
            }
        }
    };

    public GenericItemHandler(GenericTileEntity te, ContainerFactory factory) {
        tileEntity = te;
        containerFactory = factory;
        stacks = new ItemStacksResourceHandler(factory.getContainerSlots());
    }

    @Override public int size() { return stacks.size(); }
    @Override public ItemResource getResource(int index) { return stacks.getResource(index); }
    @Override public long getAmountAsLong(int index) { return stacks.getAmountAsLong(index); }
    @Override public long getCapacityAsLong(int index, ItemResource resource) {
        java.util.Objects.checkIndex(index, size());
        if (isGhost(index)) return 0;
        return Math.min(getSlotLimit(index), resource.isEmpty() ? Item.ABSOLUTE_MAX_STACK_SIZE : resource.getMaxStackSize());
    }
    @Override public boolean isValid(int index, ItemResource resource) {
        java.util.Objects.checkIndex(index, size());
        return !resource.isEmpty() && isItemValid(index, resource.toStack());
    }
    @Override public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        if (isGhost(index) || !isItemInsertable(index, resource.toStack()) || !isValid(index, resource)) return 0;
        var current = getResource(index);
        if (!current.isEmpty() && !current.equals(resource)) return 0;
        int inserted = (int) Math.min(amount, Math.max(0, getCapacityAsLong(index, resource) - getAmountAsLong(index)));
        if (inserted > 0) {
            transferJournal.updateSnapshots(transaction);
            setStoredStack(index, resource.toStack(getStackInSlot(index).getCount() + inserted));
        }
        return inserted;
    }
    @Override public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
        TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        var current = getStackInSlot(index);
        if (isGhost(index) || !resource.matches(current) || !isItemExtractable(index, current)) return 0;
        int extracted = Math.min(amount, current.getCount());
        if (extracted > 0) {
            transferJournal.updateSnapshots(transaction);
            setStoredStack(index, current.copyWithCount(current.getCount() - extracted));
        }
        return extracted;
    }
    @Override public void set(int index, ItemResource resource, int amount) { setStackInSlot(index, resource.toStack(amount)); }
    private boolean isGhost(int index) { return containerFactory.isGhostSlot(index) || containerFactory.isGhostOutputSlot(index); }
    private void setStoredStack(int slot, ItemStack stack) { stacks.set(slot, ItemResource.of(stack), stack.getCount()); }
    protected void onUpdate(int index, ItemStack stack) { if (tileEntity != null) tileEntity.markDirtyQuick(); }

    public int getSlots() { return size(); }
    public ItemStack getStackInSlot(int slot) { return ItemUtil.getStack(stacks, slot); }
    public ItemStacksResourceHandler getStacks() { return stacks; }
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) { setStoredStack(slot, stack); onUpdate(slot, stack); }
    public int getSlotLimit(int slot) { return 64; }
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) { return true; }
    public boolean isItemInsertable(int slot, @Nonnull ItemStack stack) { return isItemValid(slot, stack); }
    public boolean isItemExtractable(int slot, @Nonnull ItemStack stack) { return true; }
    public ContainerFactory getContainerFactory() { return containerFactory; }

    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return ItemUtil.insertItemReturnRemaining(this, slot, stack, simulate, null);
    }
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return mcjty.lib.varia.InventoryTools.extractItem(this, slot, amount, simulate);
    }
    public ItemStack decrStackSize(int index, int amount) {
        if (isGhost(index)) { setStackInSlot(index, ItemStack.EMPTY); return ItemStack.EMPTY; }
        return extractItem(index, amount, false);
    }
    public void setInventorySlotContents(int stackLimit, int index, ItemStack stack) {
        int limit = containerFactory.isGhostSlot(index) && index < 9 ? 1 : Math.max(0, stackLimit);
        setStackInSlot(index, stack.copyWithCount(Math.min(limit, stack.getCount())));
    }
    public void setStacks(List<ItemStack> items) {
        for (int i = 0; i < size(); i++) setStoredStack(i, i < items.size() ? items.get(i) : ItemStack.EMPTY);
    }
    public void applyImplicitComponents(ItemInventory inventory) { if (inventory != null) setStacks(inventory.items()); }
    public void collectImplicitComponents(DataComponentMap.Builder builder) {
        var items = new ArrayList<ItemStack>();
        for (int i = 0; i < size(); i++) items.add(getStackInSlot(i));
        builder.set(Registration.ITEM_INVENTORY, new ItemInventory(items));
    }
    // Retain the indexed 26.2 save format, including empty/ghost slot positions.
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, provider);
        var items = output.list("Items", ItemStackWithSlot.CODEC);
        for (int i = 0; i < size(); i++) {
            var stack = getStackInSlot(i);
            if (!stack.isEmpty()) items.add(new ItemStackWithSlot(i, stack));
        }
        output.putInt("Size", size());
        return output.buildResult();
    }
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        var input = TagValueInput.create(ProblemReporter.DISCARDING, provider, nbt);
        for (int i = 0; i < size(); i++) setStoredStack(i, ItemStack.EMPTY);
        input.listOrEmpty("Items", ItemStackWithSlot.CODEC).forEach(item -> {
            if (item.isValidInContainer(size())) setStoredStack(item.slot(), item.stack());
        });
    }
    public void save(CompoundTag tag, String name, HolderLookup.Provider provider) { tag.put(name, serializeNBT(provider)); }
    public void load(CompoundTag tag, String name, HolderLookup.Provider provider) {
        if (tag.contains(name)) deserializeNBT(provider, tag.getCompoundOrEmpty(name));
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
