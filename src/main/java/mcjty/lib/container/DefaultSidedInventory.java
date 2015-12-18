package mcjty.lib.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;

public interface DefaultSidedInventory extends ISidedInventory {
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
    default IChatComponent getDisplayName() {
        return null;
    }
}
