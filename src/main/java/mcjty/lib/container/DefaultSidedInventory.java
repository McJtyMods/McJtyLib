package mcjty.lib.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

public interface DefaultSidedInventory extends ISidedInventory {

    InventoryHelper getInventoryHelper();

    @Override
    default int getSizeInventory() {
        return getInventoryHelper().getCount();
    }

    @Override
    default ItemStack getStackInSlot(int index) {
        return getInventoryHelper().getStackInSlot(index);
    }

    @Override
    default ItemStack decrStackSize(int index, int count) {
        return getInventoryHelper().decrStackSize(index, count);
    }

    @Override
    default ItemStack removeStackFromSlot(int index) {
        return getInventoryHelper().removeStackFromSlot(index);
    }

    @Override
    default void setInventorySlotContents(int index, ItemStack stack) {
        getInventoryHelper().setInventorySlotContents(getInventoryStackLimit(), index, stack);
    }

    @Override
    default int[] getSlotsForFace(Direction side) {
        return new int[0];
    }

    @Override
    default boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
        return false;
    }

    @Override
    default boolean canExtractItem(int index, ItemStack stack, Direction direction) {
        return false;
    }

    @Override
    default int getInventoryStackLimit() {
        return 64;
    }

    @Override
    default void openInventory(PlayerEntity player) {

    }

    @Override
    default void closeInventory(PlayerEntity player) {

    }

    @Override
    default boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    default void clear() {

    }
}
