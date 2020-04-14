package mcjty.test;

import mcjty.lib.bindings.GenericTileEntity;
import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import net.minecraft.entity.player.PlayerEntity;

public class TestTileEntity extends GenericTileEntity implements DefaultSidedInventory {

    private InventoryHelper inventoryHelper = new InventoryHelper(this, TestContainer.factory, 1);

    @Override
    public InventoryHelper getInventoryHelper() {
        return inventoryHelper;
    }

    @Override
    public boolean isUsable(PlayerEntity player) {
        return canPlayerAccess(player);
    }
}
