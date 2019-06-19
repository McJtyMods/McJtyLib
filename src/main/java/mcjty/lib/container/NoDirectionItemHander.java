package mcjty.lib.container;

import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public class NoDirectionItemHander implements IItemHandlerModifiable {

    private final InventoryHelper helper;
    private final GenericTileEntity te;

    public NoDirectionItemHander(InventoryHelper helper, GenericTileEntity te) {
        this.helper = helper;
        this.te = te;
    }

    @Override
    public int getSlots() {
        return helper.getCount();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return helper.getStackInSlot(slot);
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

        ItemStack stackInSlot = helper.getStackInSlot(slot);

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
                    helper.setInventorySlotContents(copy.getMaxStackSize(), slot, copy);
                    te.markDirtyQuick();
                }

                return ItemStack.EMPTY;
            } else {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    ItemStack copy = stack.split(m);
                    copy.grow(stackInSlot.getCount());
                    helper.setInventorySlotContents(copy.getMaxStackSize(), slot, copy);
                    te.markDirtyQuick();
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
                    helper.setInventorySlotContents(stack.getMaxStackSize(), slot, stack.split(m));
                    te.markDirtyQuick();
                    return stack;
                } else {
                    stack.shrink(m);
                    return stack;
                }
            } else {
                if (!simulate) {
                    helper.setInventorySlotContents(stack.getMaxStackSize(), slot, stack);
                    te.markDirtyQuick();
                }
                return ItemStack.EMPTY;
            }
        }

    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = helper.getStackInSlot(slot);

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

            ItemStack decrStackSize = helper.decrStackSize(slot, m);
            te.markDirtyQuick();
            return decrStackSize;
        }
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        helper.setStackInSlot(slot, stack);
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
        return true;
    }

    public boolean isItemExtractable(int slot, @Nonnull ItemStack stack) {
        return true;
    }
}
