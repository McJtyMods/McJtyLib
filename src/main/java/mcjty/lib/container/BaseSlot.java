package mcjty.lib.container;

import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.world.item.ItemStack;
import mcjty.lib.container.IItemHandler;
import mcjty.lib.container.SlotItemHandler;
import javax.annotation.Nonnull;

public class BaseSlot extends SlotItemHandler {

    private final GenericTileEntity te;

    public BaseSlot(IItemHandler inventory, GenericTileEntity te, int index, int x, int y) {
        super(inventory, index, x, y);
        this.te = te;
    }

    @Override
    public void set(@Nonnull ItemStack stack) {
        if (te != null) {
            te.onSlotChanged(getContainerSlot(), stack);
        }
        super.set(stack);
    }

    public GenericTileEntity getTe() {
        return te;
    }
}
