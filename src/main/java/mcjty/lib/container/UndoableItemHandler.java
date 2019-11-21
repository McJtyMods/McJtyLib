package mcjty.lib.container;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class UndoableItemHandler implements IItemHandlerModifiable {

    private final IItemHandlerModifiable handler;
    private final Map<Integer, ItemStack> undo = new HashMap<>();

    public UndoableItemHandler(IItemHandlerModifiable handler) {
        this.handler = handler;
    }

    public void remember(int slot) {
        if (!undo.containsKey(slot)) {
            undo.put(slot, handler.getStackInSlot(slot).copy());
        }
    }

    public void restore() {
        for (Map.Entry<Integer, ItemStack> entry : undo.entrySet()) {
            handler.setStackInSlot(entry.getKey(), entry.getValue());
        }
        undo.clear();
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        remember(slot);
        handler.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
        return handler.getSlots();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return handler.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!simulate) {
            remember(slot);
        }
        return handler.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!simulate) {
            remember(slot);
        }
        return handler.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return handler.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return handler.isItemValid(slot, stack);
    }
}
