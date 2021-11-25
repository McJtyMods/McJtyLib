package mcjty.lib.container;

import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.ItemStackList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class GenericItemHandler implements IItemHandlerModifiable, INBTSerializable<ListNBT> {

    private final GenericTileEntity tileEntity;
    private final ContainerFactory containerFactory;
    private ItemStackList stacks;

    /**
     * Conveniance method to create a basic item handler with no restrictions
     */
    public static GenericItemHandler create(GenericTileEntity te, Supplier<ContainerFactory> factorySupplier) {
        return new GenericItemHandler(te, factorySupplier.get());
    }

    /**
     * Conveniance method to create a item handler with a predicate to test if a certain
     * slot can accept a certain item
     */
    public static GenericItemHandler create(GenericTileEntity te, Supplier<ContainerFactory> factorySupplier, BiPredicate<Integer, ItemStack> validator) {
        return new GenericItemHandler(te, factorySupplier.get()) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return validator.test(slot, stack);
            }
        };
    }

    /**
     * Conveniance method to create a item handler with a predicate to test if a certain
     * slot can accept a certain item. In addition this method allows you to control which slots are insertable and extractable
     */
    public static GenericItemHandler create(GenericTileEntity te, Supplier<ContainerFactory> factorySupplier, BiPredicate<Integer, ItemStack> validator,
                                            BiPredicate<Integer, ItemStack> insertable, BiPredicate<Integer, ItemStack> extractable) {
        return new GenericItemHandler(te, factorySupplier.get()) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return validator.test(slot, stack);
            }

            @Override
            public boolean isItemInsertable(int slot, @Nonnull ItemStack stack) {
                return insertable.test(slot, stack);
            }

            @Override
            public boolean isItemExtractable(int slot, @Nonnull ItemStack stack) {
                return extractable.test(slot, stack);
            }
        };
    }

    /**
     * Conveniance method to create a item handler with a predicate to test if a certain
     * slot can accept a certain item. In addition there is an onUpdate method
     */
    public static GenericItemHandler create(GenericTileEntity te, Supplier<ContainerFactory> factorySupplier, BiPredicate<Integer, ItemStack> validator,
                                            BiConsumer<Integer, ItemStack> onUpdate) {
        return new GenericItemHandler(te, factorySupplier.get()) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return validator.test(slot, stack);
            }

            @Override
            protected void onUpdate(int slot, ItemStack stack) {
                super.onUpdate(slot, stack);
                onUpdate.accept(slot, stack);
            }
        };
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
                if (stackLimit <= 0) {
                    stack.setCount(0);
                } else {
                    stack.setCount(stackLimit);
                }
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
    public ListNBT serializeNBT() {
        ListNBT bufferTagList = new ListNBT();
        for (ItemStack stack : stacks) {
            CompoundNBT compoundNBT = new CompoundNBT();
            if (!stack.isEmpty()) {
                stack.save(compoundNBT);
            }
            bufferTagList.add(compoundNBT);
        }
        return bufferTagList;
    }

    @Override
    public void deserializeNBT(ListNBT nbt) {
        for (int i = 0; i < nbt.size(); i++) {
            CompoundNBT compoundNBT = nbt.getCompound(i);
            if (i < stacks.size()) {
                stacks.set(i, ItemStack.of(compoundNBT));
            }
        }
    }
}
