package mcjty.lib.container;

import net.minecraft.world.item.ItemStack;

public final class ItemHandlerHelper {

    public static boolean canItemStacksStack(ItemStack a, ItemStack b) {
        return ItemStack.isSameItemSameComponents(a,b);
    }

    public static ItemStack copyStackWithSize(ItemStack s, int n) {
        return s.copyWithCount(n);
    }

    public static ItemStack insertItem(IItemHandler h, ItemStack s, boolean simulate) {

        for(int i=0;i<h.getSlots()&&!s.isEmpty();i++) s=h.insertItem(i,s,simulate);
        return s;

    }

    public static ItemStack insertItemStacked(IItemHandler h, ItemStack s, boolean simulate) {

        for(int i=0;i<h.getSlots()&&!s.isEmpty();i++) if(!h.getStackInSlot(i).isEmpty()) s=h.insertItem(i,s,simulate);

        return insertItem(h,s,simulate);

    }

}
