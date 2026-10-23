package mcjty.lib.container;

import mcjty.lib.varia.InventoryTools;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemUtil;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import java.util.HashMap;
import java.util.Map;

public class UndoableItemHandler extends DelegatingResourceHandler<ItemResource> implements IndexModifier<ItemResource> {
    private final IndexModifier<ItemResource> modifier;
    private final Map<Integer, ItemStack> undo = new HashMap<>();

    public UndoableItemHandler(ResourceHandler<ItemResource> handler) {
        super(handler);
        modifier = InventoryTools.modifier(handler);
    }
    public void remember(int slot) { undo.computeIfAbsent(slot, i -> ItemUtil.getStack(this, i)); }
    public void restore() {
        undo.forEach((slot, stack) -> modifier.set(slot, ItemResource.of(stack), stack.getCount()));
        undo.clear();
    }
    @Override public void set(int slot, ItemResource resource, int amount) { remember(slot); modifier.set(slot, resource, amount); }
    @Override public int insert(int slot, ItemResource resource, int amount, TransactionContext transaction) {
        remember(slot);
        return super.insert(slot, resource, amount, transaction);
    }
    @Override public int extract(int slot, ItemResource resource, int amount, TransactionContext transaction) {
        remember(slot);
        return super.extract(slot, resource, amount, transaction);
    }
    @Override public int insert(ItemResource resource, int amount, TransactionContext transaction) {
        net.neoforged.neoforge.transfer.TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        int inserted = 0;
        for (int slot = 0; slot < size() && inserted < amount; slot++) inserted += insert(slot, resource, amount - inserted, transaction);
        return inserted;
    }
    @Override public int extract(ItemResource resource, int amount, TransactionContext transaction) {
        net.neoforged.neoforge.transfer.TransferPreconditions.checkNonEmptyNonNegative(resource, amount);
        int extracted = 0;
        for (int slot = 0; slot < size() && extracted < amount; slot++) extracted += extract(slot, resource, amount - extracted, transaction);
        return extracted;
    }
    public void setStackInSlot(int slot, ItemStack stack) { set(slot, ItemResource.of(stack), stack.getCount()); }
    public int getSlots() { return size(); }
    public ItemStack getStackInSlot(int slot) { return ItemUtil.getStack(this, slot); }
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) { return InventoryTools.insertItem(this, slot, stack, simulate); }
    public ItemStack extractItem(int slot, int amount, boolean simulate) { return InventoryTools.extractItem(this, slot, amount, simulate); }
    public int getSlotLimit(int slot) { return getCapacityAsInt(slot, ItemResource.EMPTY); }
    public boolean isItemValid(int slot, ItemStack stack) { return isValid(slot, ItemResource.of(stack)); }
}
