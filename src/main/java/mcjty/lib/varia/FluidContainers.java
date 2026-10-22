package mcjty.lib.varia;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;

public final class FluidContainers {

    private static SingleVariantStorage<ItemVariant> slot(ItemStack stack) {
        return new SingleVariantStorage<>() {

            {
                variant=ItemVariant.of(stack);
                amount=stack.getCount();
            }

            protected ItemVariant getBlankVariant() {
                return ItemVariant.blank();
            }
            protected long getCapacity(ItemVariant variant) {
                return 64;
            }

        };
    }

    public static IFluidHandler handler(ItemStack stack) {
        return CapabilityTools.fluidHandler(FluidStorage.ITEM.find(stack,ContainerItemContext.withConstant(stack)));
    }

    public static ItemStack fill(FluidStack fluid,ItemStack stack) {
        var slot=slot(stack.copyWithCount(1));
        var storage=FluidStorage.ITEM.find(stack,ContainerItemContext.ofSingleSlot(slot));
        if(storage==null)return ItemStack.EMPTY;
        try(var tx=Transaction.openOuter()) {
            if(storage.insert(fluid.variant(),fluid.getAmount()*81L,tx)==0)return ItemStack.EMPTY;
            tx.commit();
            return slot.variant.toStack((int)slot.amount);
        }
    }

    public static ItemStack drain(ItemStack stack) {
        var slot=slot(stack.copyWithCount(1));
        var storage=FluidStorage.ITEM.find(stack,ContainerItemContext.ofSingleSlot(slot));
        if(storage==null)return ItemStack.EMPTY;
        try(var tx=Transaction.openOuter()) {
            for(var view:storage)if(!view.isResourceBlank()&&view.extract(view.getResource(),Long.MAX_VALUE,tx)>0) {
                tx.commit();
                return slot.variant.toStack((int)slot.amount);
            }
            return ItemStack.EMPTY;
        }
    }

}
