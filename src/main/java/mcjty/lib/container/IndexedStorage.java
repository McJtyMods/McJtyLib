package mcjty.lib.container;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import java.util.Iterator;
import java.util.stream.IntStream;

/** Fabric storage with addressable views for the library's slot-oriented helpers. */
public interface IndexedStorage<T> extends Storage<T> {

    int size();
    T getResource(int index);
    long getAmountAsLong(int index);
    long getCapacityAsLong(int index,T resource);

    boolean isValid(int index,T resource);

    long insert(int index,T resource,long amount,TransactionContext tx);

    long extract(int index,T resource,long amount,TransactionContext tx);

    @Override default long insert(T resource,long amount,TransactionContext tx) {
        if(amount<0)throw new IllegalArgumentException("Negative amount");
        long n=0;
        for(int i=0;i<size()&&n<amount;i++)n+=insert(i,resource,amount-n,tx);
        return n;
    }

    @Override default long extract(T resource,long amount,TransactionContext tx) {
        if(amount<0)throw new IllegalArgumentException("Negative amount");
        long n=0;
        for(int i=0;i<size()&&n<amount;i++)n+=extract(i,resource,amount-n,tx);
        return n;
    }

    @Override default Iterator<StorageView<T>> iterator() {
        return IntStream.range(0,size()).<StorageView<T>>mapToObj(i->new StorageView<>() {

            public T getResource() {
                return IndexedStorage.this.getResource(i);
            }
            public long getAmount() {
                return getAmountAsLong(i);
            }

            public long getCapacity() {
                return getCapacityAsLong(i,getResource());
            }
            public boolean isResourceBlank() {
                return getAmount()==0;
            }

            public long extract(T r,long n,TransactionContext tx) {
                return IndexedStorage.this.extract(i,r,n,tx);
            }

        }).iterator();
    }

}
