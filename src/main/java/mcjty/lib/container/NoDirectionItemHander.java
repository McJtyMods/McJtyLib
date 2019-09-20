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

public class NoDirectionItemHander implements IItemHandlerModifiable, INBTSerializable<ListNBT> {

    private final InventoryHelper helper;
    private final GenericTileEntity te;

    // Called when something changed
    protected void onUpdate(int index) {
        te.markDirtyQuick();
    }

    public NoDirectionItemHander(GenericTileEntity te, ContainerFactory factory) {
        this.helper = new InventoryHelper(te, factory, factory.getContainerSlots());
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
                    onUpdate(slot);
                }

                return ItemStack.EMPTY;
            } else {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    ItemStack copy = stack.split(m);
                    copy.grow(stackInSlot.getCount());
                    helper.setInventorySlotContents(copy.getMaxStackSize(), slot, copy);
                    onUpdate(slot);
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
                    onUpdate(slot);
                    return stack;
                } else {
                    stack.shrink(m);
                    return stack;
                }
            } else {
                if (!simulate) {
                    helper.setInventorySlotContents(stack.getMaxStackSize(), slot, stack);
                    onUpdate(slot);
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

//        if (!isItemExtractable(slot, stackInSlot)) {
//            return ItemStack.EMPTY;
//        }

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
            onUpdate(slot);
            return decrStackSize;
        }
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        helper.setStackInSlot(slot, stack);
        onUpdate(slot);
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
        return helper.getContainerFactory().isInputSlot(slot) || helper.getContainerFactory().isSpecificItemSlot(slot);
    }

    public boolean isItemExtractable(int slot, @Nonnull ItemStack stack) {
        return helper.getContainerFactory().isOutputSlot(slot);
    }

    @Override
    public ListNBT serializeNBT() {
        ItemStackList list = helper.getStacks();
        ListNBT bufferTagList = new ListNBT();
        for (ItemStack stack : list) {
            CompoundNBT compoundNBT = new CompoundNBT();
            if (!stack.isEmpty()) {
                stack.write(compoundNBT);
            }
            bufferTagList.add(compoundNBT);
        }
        return bufferTagList;
    }

    @Override
    public void deserializeNBT(ListNBT nbt) {
        ItemStackList list = helper.getStacks();
        for (int i = 0; i < nbt.size(); i++) {
            CompoundNBT compoundNBT = nbt.getCompound(i);
            if (i < list.size()) {
                list.set(i, ItemStack.read(compoundNBT));
            }
        }
    }
}
