package mcjty.lib.container;

import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BaseSlot extends SlotItemHandler {

    private final GenericTileEntity te;

    public BaseSlot(IItemHandler inventory, GenericTileEntity te, int index, int x, int y) {
        super(inventory, index, x, y);
        this.te = te;
    }

    @Override
    public void putStack(ItemStack stack) {
        if (te != null) {
            te.onSlotChanged(getSlotIndex(), stack);
        }
        super.putStack(stack);
    }

    public GenericTileEntity getTe() {
        return te;
    }
}
