package mcjty.lib.container;

import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class CraftingSlot extends SlotItemHandler {
    private final GenericCrafter crafter;
    private final GenericTileEntity te;

    public CraftingSlot(IItemHandler inventory, GenericTileEntity te, int index, int x, int y, GenericCrafter crafter) {
        super(inventory, index, x, y);
        this.crafter = crafter;
        this.te = te;
    }

    @Override
    public void putStack(ItemStack stack) {
        if (te != null) {
            te.onSlotChanged(getSlotIndex(), stack);
        }
        super.putStack(stack);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack onTake(PlayerEntity thePlayer, ItemStack stack) {
        crafter.craftItem();
        return super.onTake(thePlayer, stack);
    }
}
