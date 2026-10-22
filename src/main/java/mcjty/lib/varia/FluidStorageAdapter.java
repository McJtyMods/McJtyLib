package mcjty.lib.varia;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.*;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import java.util.*;

final class FluidStorageAdapter implements IFluidHandler {

    private final Storage<FluidVariant> storage;

    FluidStorageAdapter(Storage<FluidVariant> storage) {
        this.storage=storage;
    }

    private List<StorageView<FluidVariant>> views() {
        var result=new ArrayList<StorageView<FluidVariant>>();
        storage.forEach(result::add);
        return result;
    }

    public int getTanks() {
        return views().size();
    }

    public FluidStack getFluidInTank(int i) {
        var v=views().get(i);
        return new FluidStack(v.getResource(),(int)Math.min(Integer.MAX_VALUE,v.getAmount()/81));
    }

    public int getTankCapacity(int i) {
        return (int)Math.min(Integer.MAX_VALUE,views().get(i).getCapacity()/81);
    }

    public boolean isFluidValid(int i,FluidStack s) {
        return storage.supportsInsertion();
    }

    private long transfer(FluidStack stack,FluidAction action,boolean insert) {

        long available;

        try(var probe=Transaction.openOuter()) {
            available=insert?storage.insert(stack.variant(),stack.getAmount()*81L,probe):storage.extract(stack.variant(),stack.getAmount()*81L,probe);
        }

        long whole=available-available%81;

        if(action.simulate()||whole==0)return whole;

        try(var tx=Transaction.openOuter()) {

            long moved=insert?storage.insert(stack.variant(),whole,tx):storage.extract(stack.variant(),whole,tx);

            if(moved!=whole)return 0;

            tx.commit();
            return moved;

        }

    }

    public int fill(FluidStack stack,FluidAction action) {
        return (int)(transfer(stack,action,true)/81);
    }

    public FluidStack drain(FluidStack stack,FluidAction action) {
        return stack.copyWithAmount((int)(transfer(stack,action,false)/81));
    }

    public FluidStack drain(int n,FluidAction a) {
        for(var v:storage)if(!v.isResourceBlank())return drain(new FluidStack(v.getResource(),n),a);
        return FluidStack.EMPTY;
    }

}
