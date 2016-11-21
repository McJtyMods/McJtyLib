package mcjty.lib.container;

import mcjty.lib.inventory.CompatSidedInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;

public interface DefaultSidedInventory extends CompatSidedInventory {

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
    default int[] getSlotsForFace(EnumFacing side) {
        return new int[0];
    }

    @Override
    default boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return false;
    }

    @Override
    default boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }

    @Override
    default int getInventoryStackLimit() {
        return 64;
    }

    @Override
    default void openInventory(EntityPlayer player) {

    }

    @Override
    default void closeInventory(EntityPlayer player) {

    }

    @Override
    default boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    default int getField(int id) {
        return 0;
    }

    @Override
    default void setField(int id, int value) {

    }

    @Override
    default int getFieldCount() {
        return 0;
    }

    @Override
    default void clear() {

    }

    @Override
    default String getName() {
        return "inventory";
    }

    @Override
    default boolean hasCustomName() {
        return false;
    }

    @Override
    default ITextComponent getDisplayName() {
        return null;
    }

}
