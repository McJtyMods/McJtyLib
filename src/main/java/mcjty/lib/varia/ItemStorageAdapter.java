package mcjty.lib.varia;

import java.util.ArrayList;
import java.util.List;
import mcjty.lib.container.IItemHandler;
import mcjty.lib.container.IndexedStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.SlottedStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;

/** A live slot view over native Fabric inventories. */
final class ItemStorageAdapter implements IItemHandler {

    private final Storage<ItemVariant> storage;

    ItemStorageAdapter(Storage<ItemVariant> storage) {
        this.storage = storage;
    }

    private List<StorageView<ItemVariant>> views() {
        var result = new ArrayList<StorageView<ItemVariant>>();
        storage.forEach(result::add);
        return result;
    }

    @Override
    public int getSlots() {
        return storage instanceof IndexedStorage<ItemVariant> indexed ? indexed.size() : views().size();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (storage instanceof IndexedStorage<ItemVariant> indexed) {
            return indexed.getResource(slot).toStack((int) Math.min(Integer.MAX_VALUE, indexed.getAmountAsLong(slot)));
        }
        var view = views().get(slot);
        return view.getResource().toStack((int) Math.min(Integer.MAX_VALUE, view.getAmount()));
    }

    @Override
    public int getSlotLimit(int slot) {
        if (storage instanceof IndexedStorage<ItemVariant> indexed) {
            return (int) Math.min(Integer.MAX_VALUE, indexed.getCapacityAsLong(slot, indexed.getResource(slot)));
        }
        return (int) Math.min(Integer.MAX_VALUE, views().get(slot).getCapacity());
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return !stack.isEmpty() && (storage instanceof IndexedStorage<ItemVariant> indexed
                ? indexed.isValid(slot, ItemVariant.of(stack)) : storage.supportsInsertion());
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        try (var tx = Transaction.openOuter()) {
            long moved;
            if (storage instanceof IndexedStorage<ItemVariant> indexed) {
                moved = indexed.insert(slot, ItemVariant.of(stack), stack.getCount(), tx);
            } else if (storage instanceof SlottedStorage<ItemVariant> slotted) {
                moved = slotted.getSlot(slot).insert(ItemVariant.of(stack), stack.getCount(), tx);
            } else if (slot == 0) {
                moved = storage.insert(ItemVariant.of(stack), stack.getCount(), tx);
            } else {
                return stack;
            }
            if (!simulate) tx.commit();
            return stack.copyWithCount(stack.getCount() - (int) moved);
        }
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount <= 0) return ItemStack.EMPTY;
        ItemStack current = getStackInSlot(slot);
        if (current.isEmpty()) return ItemStack.EMPTY;
        ItemVariant resource = ItemVariant.of(current);
        try (var tx = Transaction.openOuter()) {
            long moved = storage instanceof IndexedStorage<ItemVariant> indexed
                    ? indexed.extract(slot, resource, amount, tx) : views().get(slot).extract(resource, amount, tx);
            if (!simulate) tx.commit();
            return resource.toStack((int) moved);
        }
    }
}
