package mcjty.lib.container;

import net.minecraft.world.item.ItemStack;

public class RangedWrapper implements IItemHandlerModifiable {

    private final IItemHandler source;
    private final int start;
    private final int end;

    public RangedWrapper(IItemHandler source, int start, int end) {
        this.source = source;
        this.start = start;
        this.end = end;
    }

    @Override
    public int getSlots() {
        return end - start;
    }

    private IItemHandler handler(int slot) {
        return source;
    }

    private int index(int slot) {
        if (slot < 0 || slot >= getSlots()) throw new IndexOutOfBoundsException(slot);
        return start + slot;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return handler(slot).getStackInSlot(index(slot));
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return handler(slot).insertItem(index(slot), stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return handler(slot).extractItem(index(slot), amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return handler(slot).getSlotLimit(index(slot));
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return handler(slot).isItemValid(index(slot), stack);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        ((IItemHandlerModifiable) handler(slot)).setStackInSlot(index(slot), stack);
    }
}
