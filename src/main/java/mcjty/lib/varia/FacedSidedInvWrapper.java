package mcjty.lib.varia;

import mcjty.lib.compat.CompatItemHandlerModifiable;
import mcjty.lib.tools.ItemStackTools;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

/**
 * Works on a ISidedInventory. Uses normal IInventory methods if side == null
 * and otherwise the correct sided api from ISidedInventory
 */
public class FacedSidedInvWrapper implements CompatItemHandlerModifiable {
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
        return i == -1 ? ItemStackTools.getEmptyStack() : inv.getStackInSlot(i);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {

        if (ItemStackTools.isEmpty(stack)) {
            return ItemStackTools.getEmptyStack();
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
        if (ItemStackTools.isValid(stackInSlot)) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot)) {
                return stack;
            }

            m = Math.min(stack.getMaxStackSize(), inv.getInventoryStackLimit()) - ItemStackTools.getStackSize(stackInSlot);

            if (ItemStackTools.getStackSize(stack) <= m) {
                if (!simulate) {
                    ItemStack copy = stack.copy();
                    ItemStackTools.incStackSize(copy, ItemStackTools.getStackSize(stackInSlot));
                    inv.setInventorySlotContents(slot, copy);
                }

                return ItemStackTools.getEmptyStack();
            } else {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    ItemStack copy = stack.splitStack(m);
                    ItemStackTools.incStackSize(copy, ItemStackTools.getStackSize(stackInSlot));
                    inv.setInventorySlotContents(slot, copy);
                    return stack;
                } else {
                    ItemStackTools.incStackSize(stack, -m);
                    return stack;
                }
            }
        } else {
            m = Math.min(stack.getMaxStackSize(), inv.getInventoryStackLimit());
            if (m < ItemStackTools.getStackSize(stack)) {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    inv.setInventorySlotContents(slot, stack.splitStack(m));
                    return stack;
                } else {
                    ItemStackTools.incStackSize(stack, -m);
                    return stack;
                }
            } else {
                if (!simulate) {
                    inv.setInventorySlotContents(slot, stack);
                }
                return ItemStackTools.getEmptyStack();
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
            return ItemStackTools.getEmptyStack();
        }

        int slot1 = getSlot(inv, slot, facing);

        if (slot1 == -1) {
            return ItemStackTools.getEmptyStack();
        }

        ItemStack stackInSlot = inv.getStackInSlot(slot1);

        if (ItemStackTools.isEmpty(stackInSlot)) {
            return ItemStackTools.getEmptyStack();
        }

        if (facing != null && !inv.canExtractItem(slot1, stackInSlot, facing)) {
            return ItemStackTools.getEmptyStack();
        }

        if (simulate) {
            if (ItemStackTools.getStackSize(stackInSlot) < amount) {
                return stackInSlot.copy();
            } else {
                ItemStack copy = stackInSlot.copy();
                ItemStackTools.setStackSize(copy, amount);
                return copy;
            }
        } else {
            int m = Math.min(ItemStackTools.getStackSize(stackInSlot), amount);
            return inv.decrStackSize(slot1, m);
        }
    }

    @Override
    public int getSlotMaxLimit() {
        return 64;
    }
}