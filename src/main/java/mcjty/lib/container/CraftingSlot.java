package mcjty.lib.container;

import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class CraftingSlot extends Slot {
    private final GenericCrafter crafter;

    public CraftingSlot(IInventory inventory, int index, int x, int y, GenericCrafter crafter) {
        super(inventory, index, x, y);
        this.crafter = crafter;
    }

    @Override
    public void putStack(ItemStack stack) {
        if (inventory instanceof GenericTileEntity) {
            GenericTileEntity genericTileEntity = (GenericTileEntity) inventory;
            genericTileEntity.onSlotChanged(getSlotIndex(), stack);
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
