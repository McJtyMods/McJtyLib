package mcjty.lib.container;

import mcjty.lib.compat.CompatSlot;
import mcjty.lib.entity.GenericTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class CraftingSlot extends CompatSlot {
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
    protected ItemStack onPickup(EntityPlayer player, ItemStack stack) {
        crafter.craftItem();
        return super.onPickup(player, stack);
    }
}
