package mcjty.lib.container;

import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.ItemStackList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

public class GenericItemHandler implements IItemHandlerModifiable, INBTSerializable<ListTag> {

    private final GenericTileEntity tileEntity;
    private final ContainerFactory containerFactory;
    private final ItemStackList stacks;

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
        stacks = ItemStackList.create(containerFactory.getContainerSlots());
    }

    @Override
    public int getSlots() {
        return stacks.size();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot >= stacks.size()) {
            return ItemStack.EMPTY;
        }
        return stacks.get(slot);
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

            if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot)) {
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
        if (index >= stacks.size()) {
            return;
        }

        if (containerFactory.isGhostSlot(index)) {
            if (!stack.isEmpty()) {
                ItemStack stack1 = stack.copy();
                if (index < 9) {
                    stack1.setCount(1);
                }
                stacks.set(index, stack1);
            } else {
                stacks.set(index, ItemStack.EMPTY);
            }
        } else if (containerFactory.isGhostOutputSlot(index)) {
            if (!stack.isEmpty()) {
                stacks.set(index, stack.copy());
            } else {
                stacks.set(index, ItemStack.EMPTY);
            }
        } else {
            stacks.set(index, stack);
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
        if (index >= stacks.size()) {
            return ItemStack.EMPTY;
        }

        if (containerFactory.isGhostSlot(index) || containerFactory.isGhostOutputSlot(index)) {
            ItemStack old = stacks.get(index);
            stacks.set(index, ItemStack.EMPTY);
            if (old.isEmpty()) {
                return ItemStack.EMPTY;
            }
            old.setCount(0);
            return old;
        } else {
            if (!stacks.get(index).isEmpty()) {
                if (stacks.get(index).getCount() <= amount) {
                    ItemStack old = stacks.get(index);
                    stacks.set(index, ItemStack.EMPTY);
                    tileEntity.setChanged();
                    return old;
                }
                ItemStack its = stacks.get(index).split(amount);
                if (stacks.get(index).isEmpty()) {
                    stacks.set(index, ItemStack.EMPTY);
                }
                tileEntity.setChanged();
                return its;
            }
            return ItemStack.EMPTY;
        }
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        stacks.set(slot, stack);
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

    @Override
    public ListTag serializeNBT() {
        ListTag bufferTagList = new ListTag();
        for (ItemStack stack : stacks) {
            CompoundTag compoundNBT = new CompoundTag();
            if (!stack.isEmpty()) {
                stack.save(compoundNBT);
            }
            bufferTagList.add(compoundNBT);
        }
        return bufferTagList;
    }

    @Override
    public void deserializeNBT(ListTag nbt) {
        for (int i = 0; i < nbt.size(); i++) {
            CompoundTag compoundNBT = nbt.getCompound(i);
            if (i < stacks.size()) {
                stacks.set(i, ItemStack.of(compoundNBT));
            }
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
