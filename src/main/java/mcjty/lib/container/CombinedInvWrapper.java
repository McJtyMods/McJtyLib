package mcjty.lib.container;

import java.util.List;
import net.minecraft.world.item.ItemStack;

public class CombinedInvWrapper implements IItemHandlerModifiable {

    private final List<IItemHandler> handlers;

    public CombinedInvWrapper(IItemHandler... handlers) {
        this.handlers = List.of(handlers);
    }

    @Override
    public int getSlots() {
        return handlers.stream().mapToInt(IItemHandler::getSlots).sum();
    }

    private IItemHandler handler(int slot) {
        for (IItemHandler handler : handlers) {
            if (slot < handler.getSlots()) return handler;
            slot -= handler.getSlots();
        }
        throw new IndexOutOfBoundsException(slot);
    }

    private int index(int slot) {
        for (IItemHandler handler : handlers) {
            if (slot < handler.getSlots()) return slot;
            slot -= handler.getSlots();
        }
        throw new IndexOutOfBoundsException(slot);
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
