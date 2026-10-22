package mcjty.lib.container;

import net.minecraft.world.item.ItemStack;

/** Slot-oriented inventory access for McJtyLib containers. Automation uses Fabric Storage. */
public interface IItemHandler {

    int getSlots();
    ItemStack getStackInSlot(int slot);

    ItemStack insertItem(int slot, ItemStack stack, boolean simulate);

    ItemStack extractItem(int slot, int amount, boolean simulate);

    int getSlotLimit(int slot);
    boolean isItemValid(int slot, ItemStack stack);

}
