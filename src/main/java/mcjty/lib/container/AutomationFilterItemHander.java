package mcjty.lib.container;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import mcjty.lib.container.IItemHandlerModifiable;
import javax.annotation.Nonnull;

/**
 * This item handler is meant for automation and is usually returned as the result
 * of TE.getCapability(). Specific methods to override are 'canAutomationInsert'
 * and 'canAutomationExtract'. Most TE's have another IItemHandler that is used
 * for the container so that the player can interact with slots even if automation can't
 */

import mcjty.lib.container.IndexedStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import team.reborn.energy.api.EnergyStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

public class AutomationFilterItemHander implements IItemHandlerModifiable, IndexedStorage<ItemVariant> {

    private final GenericItemHandler wrapped;

    public AutomationFilterItemHander(GenericItemHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        wrapped.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
        return wrapped.getSlots();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return wrapped.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!canAutomationInsert(slot)) {
            return stack;
        }
        return wrapped.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!canAutomationExtract(slot)) {
            return ItemStack.EMPTY;
        }
        return wrapped.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return wrapped.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return wrapped.isItemValid(slot, stack);
    }

    public boolean canAutomationInsert(int slot) {
        return wrapped.getContainerFactory().isInputSlot(slot);
    }

    public boolean canAutomationExtract(int slot) {
        return wrapped.getContainerFactory().isOutputSlot(slot);
    }

    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return wrapped.serializeNBT(provider);
    }

    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        wrapped.deserializeNBT(provider, nbt);
    }
    @Override public int size() { return wrapped.size(); }
    @Override public ItemVariant getResource(int index) { return wrapped.getResource(index); }
    @Override public long getAmountAsLong(int index) { return wrapped.getAmountAsLong(index); }
    @Override public long getCapacityAsLong(int index, ItemVariant resource) { return wrapped.getCapacityAsLong(index, resource); }
    @Override public boolean isValid(int index, ItemVariant resource) { return wrapped.isValid(index, resource); }
    @Override public long insert(int index, ItemVariant resource, long amount, TransactionContext transaction) {
        return canAutomationInsert(index) ? wrapped.insert(index, resource, amount, transaction) : 0;
    }
    @Override public long extract(int index, ItemVariant resource, long amount, TransactionContext transaction) {
        return canAutomationExtract(index) ? wrapped.extract(index, resource, amount, transaction) : 0;
    }
}
