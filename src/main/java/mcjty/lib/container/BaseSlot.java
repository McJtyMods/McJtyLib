package mcjty.lib.container;

import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import mcjty.lib.varia.InventoryTools;

import javax.annotation.Nonnull;

public class BaseSlot extends ResourceHandlerSlot {

    private final GenericTileEntity te;

    public BaseSlot(ResourceHandler<ItemResource> inventory, GenericTileEntity te, int index, int x, int y) {
        super(inventory, InventoryTools.modifier(inventory), index, x, y);
        this.te = te;
    }

    @Override
    protected void setStackCopy(@Nonnull ItemStack stack) {
        if (te != null) {
            te.onSlotChanged(getSlotIndex(), stack);
        }
        super.setStackCopy(stack);
    }

    public GenericTileEntity getTe() {
        return te;
    }
}
