package mcjty.lib.varia;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * Works on a ISidedInventory. Uses normal IInventory methods if side == null
 * and otherwise the correct sided api from ISidedInventory
 */
public class FacedSidedInvWrapper implements IItemHandlerModifiable {
    private final ISidedInventory inv;
    private final EnumFacing facing;

    // This version allows a 'null' facing
    public FacedSidedInvWrapper(ISidedInventory inv, EnumFacing facing) {
        this.inv = inv;
        this.facing = facing;
    }

    public static int getSlot(ISidedInventory inv, int slot, EnumFacing facing) {
        if (facing == null) {
            return slot;
        }
        int[] slots = inv.getSlotsForFace(facing);
        if (slot < slots.length) {
            return slots[slot];
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FacedSidedInvWrapper that = (FacedSidedInvWrapper) o;

        if (inv != null ? !inv.equals(that.inv) : that.inv != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = inv != null ? inv.hashCode() : 0;
        result = 31 * result + (facing != null ? facing.hashCode() : 0);
        return result;
    }

    @Override
    public int getSlots() {
        if (facing == null) {
            return inv.getSizeInventory();
        } else {
            return inv.getSlotsForFace(facing).length;
        }
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        int i = getSlot(inv, slot, facing);
        return i == -1 ? ItemStack.EMPTY : inv.getStackInSlot(i);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {

        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        slot = getSlot(inv, slot, facing);

        if (slot == -1) {
            return stack;
        }

        if (!inv.isItemValidForSlot(slot, stack) || (facing != null && !inv.canInsertItem(slot, stack, facing))) {
            return stack;
        }

        ItemStack stackInSlot = inv.getStackInSlot(slot);

        int m;
        if (!stackInSlot.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot)) {
                return stack;
            }

            m = Math.min(stack.getMaxStackSize(), inv.getInventoryStackLimit()) - stackInSlot.getCount();

            if (stack.getCount() <= m) {
                if (!simulate) {
                    ItemStack copy = stack.copy();
                    copy.grow(stackInSlot.getCount());
                    inv.setInventorySlotContents(slot, copy);
                }

                return ItemStack.EMPTY;
            } else {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    ItemStack copy = stack.splitStack(m);
                    copy.grow(stackInSlot.getCount());
                    inv.setInventorySlotContents(slot, copy);
                    return stack;
                } else {
                    int amount = -m;
                    stack.grow(amount);
                    return stack;
                }
            }
        } else {
            m = Math.min(stack.getMaxStackSize(), inv.getInventoryStackLimit());
            if (m < stack.getCount()) {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    inv.setInventorySlotContents(slot, stack.splitStack(m));
                    return stack;
                } else {
                    int amount = -m;
                    stack.grow(amount);
                    return stack;
                }
            } else {
                if (!simulate) {
                    inv.setInventorySlotContents(slot, stack);
                }
                return ItemStack.EMPTY;
            }
        }

    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        int i = getSlot(inv, slot, facing);
        inv.setInventorySlotContents(i, stack);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        }

        int slot1 = getSlot(inv, slot, facing);

        if (slot1 == -1) {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = inv.getStackInSlot(slot1);

        if (stackInSlot.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (facing != null && !inv.canExtractItem(slot1, stackInSlot, facing)) {
            return ItemStack.EMPTY;
        }

        if (simulate) {
            if (stackInSlot.getCount() < amount) {
                return stackInSlot.copy();
            } else {
                ItemStack copy = stackInSlot.copy();
                if (amount <= 0) {
                    copy.setCount(0);
                } else {
                    copy.setCount(amount);
                }
                return copy;
            }
        } else {
            int m = Math.min(stackInSlot.getCount(), amount);
            return inv.decrStackSize(slot1, m);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }
}