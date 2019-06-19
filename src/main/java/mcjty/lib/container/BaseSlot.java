package mcjty.lib.container;

import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BaseSlot extends SlotItemHandler {
    public BaseSlot(IItemHandler inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public void putStack(ItemStack stack) {
        if (inventory instanceof GenericTileEntity) {
            GenericTileEntity genericTileEntity = (GenericTileEntity) inventory;
            genericTileEntity.onSlotChanged(getSlotIndex(), stack);
        }
        super.putStack(stack);
    }
}
