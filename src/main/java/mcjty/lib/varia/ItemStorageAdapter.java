package mcjty.lib.varia;

import mcjty.lib.container.IItemHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.*;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;
import java.util.*;

/** A live slot view over third-party Fabric inventories. */
final class ItemStorageAdapter implements IItemHandler {

    private final Storage<ItemVariant> storage;

    ItemStorageAdapter(Storage<ItemVariant> storage) {
        this.storage=storage;
    }

    private List<StorageView<ItemVariant>> views() {
        var result=new ArrayList<StorageView<ItemVariant>>();
        storage.forEach(result::add);
        return result;
    }

    public int getSlots() {
        return views().size();
    }

    public ItemStack getStackInSlot(int i) {
        var v=views().get(i);
        return v.getResource().toStack((int)Math.min(v.getAmount(),Integer.MAX_VALUE));
    }

    public int getSlotLimit(int i) {
        return (int)Math.min(views().get(i).getCapacity(),Integer.MAX_VALUE);
    }

    public boolean isItemValid(int i,ItemStack s) {
        return storage.supportsInsertion();
    }

    public ItemStack insertItem(int i,ItemStack s,boolean simulate) {

        Storage<ItemVariant> target;

        if(storage instanceof SlottedStorage<ItemVariant> slotted)target=slotted.getSlot(i);

        else if(i==0)target=storage;
        else return s;

        try(var tx=Transaction.openOuter()) {
            long n=target.insert(ItemVariant.of(s),s.getCount(),tx);
            if(!simulate)tx.commit();
            return s.copyWithCount(s.getCount()-(int)n);
        }

    }

    public ItemStack extractItem(int i,int n,boolean simulate) {
        var v=views().get(i);
        var item=v.getResource();
        try(var tx=Transaction.openOuter()) {
            long moved=v.extract(item,n,tx);
            if(!simulate)tx.commit();
            return item.toStack((int)moved);
        }
    }

}
