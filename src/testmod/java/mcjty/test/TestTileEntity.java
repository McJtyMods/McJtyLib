package mcjty.test;

import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.bindings.GenericTileEntity;
import net.minecraft.entity.player.EntityPlayer;

public class TestTileEntity extends GenericTileEntity implements DefaultSidedInventory {

    private InventoryHelper inventoryHelper = new InventoryHelper(this, TestContainer.factory, 1);

    @Override
    public InventoryHelper getInventoryHelper() {
        return inventoryHelper;
    }

    @Override
    public boolean isUsable(EntityPlayer player) {
        return canPlayerAccess(player);
    }
}
