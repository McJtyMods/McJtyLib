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
        return i == -1 ? null : inv.getStackInSlot(i);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {

        if (stack == null) {
            return null;
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
        if (stackInSlot != null) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot)) {
                return stack;
            }

            m = Math.min(stack.getMaxStackSize(), inv.getInventoryStackLimit()) - stackInSlot.stackSize;

            if (stack.stackSize <= m) {
                if (!simulate) {
                    ItemStack copy = stack.copy();
                    copy.stackSize += stackInSlot.stackSize;
                    inv.setInventorySlotContents(slot, copy);
                }

                return null;
            } else {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    ItemStack copy = stack.splitStack(m);
                    copy.stackSize += stackInSlot.stackSize;
                    inv.setInventorySlotContents(slot, copy);
                    return stack;
                } else {
                    stack.stackSize -= m;
                    return stack;
                }
            }
        } else {
            m = Math.min(stack.getMaxStackSize(), inv.getInventoryStackLimit());
            if (m < stack.stackSize) {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    inv.setInventorySlotContents(slot, stack.splitStack(m));
                    return stack;
                } else {
                    stack.stackSize -= m;
                    return stack;
                }
            } else {
                if (!simulate) {
                    inv.setInventorySlotContents(slot, stack);
                }
                return null;
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
            return null;
        }

        int slot1 = getSlot(inv, slot, facing);

        if (slot1 == -1) {
            return null;
        }

        ItemStack stackInSlot = inv.getStackInSlot(slot1);

        if (stackInSlot == null) {
            return null;
        }

        if (facing != null && !inv.canExtractItem(slot1, stackInSlot, facing)) {
            return null;
        }

        if (simulate) {
            if (stackInSlot.stackSize < amount) {
                return stackInSlot.copy();
            } else {
                ItemStack copy = stackInSlot.copy();
                copy.stackSize = amount;
                return copy;
            }
        } else {
            int m = Math.min(stackInSlot.stackSize, amount);
            return inv.decrStackSize(slot1, m);
        }
    }
}