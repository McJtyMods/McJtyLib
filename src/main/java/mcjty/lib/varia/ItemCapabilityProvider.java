package mcjty.lib.varia;

import mcjty.lib.api.power.IBigPower;
import net.minecraft.world.item.ItemStack;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import team.reborn.energy.api.EnergyStorage;

public final class ItemCapabilityProvider implements IBigPower {

    private final ItemStack stack;
    private final IEnergyItem item;

    public ItemCapabilityProvider(ItemStack stack,IEnergyItem item) {
        this.stack=stack;
        this.item=item;
    }

    public long getStoredPower() {
        return item.getEnergyStoredL(stack);
    }
    public long getCapacity() {
        return item.getMaxEnergyStoredL(stack);
    }

    public static void register() {
        EnergyStorage.ITEM.registerFallback((stack,context)->stack.getItem() instanceof IEnergyItem item ? new Storage(context,item):null);
    }

    private record Storage(ContainerItemContext context,IEnergyItem item) implements EnergyStorage {

        private ItemStack current() {
            return context.getItemVariant().toStack();
        }

        public long getAmount() {
            return item.getEnergyStoredL(current());
        }
        public long getCapacity() {
            return item.getMaxEnergyStoredL(current());
        }

        public long insert(long n,TransactionContext tx) {
            return transfer(n,tx,true);
        }
        public long extract(long n,TransactionContext tx) {
            return transfer(n,tx,false);
        }

        private long transfer(long n,TransactionContext tx,boolean insert) {

            if(n<0)throw new IllegalArgumentException("Negative energy amount");
            if(n==0)return 0;

            ItemStack copy=current();
            long moved=insert?item.receiveEnergyL(copy,n,false):item.extractEnergyL(copy,n,false);

            if(moved<=0)return 0;

            try(var nested=tx.openNested()) {
                if(context.exchange(ItemVariant.of(copy),1,nested)!=1)return 0;
                nested.commit();
                return moved;
            }

        }

    }

}
