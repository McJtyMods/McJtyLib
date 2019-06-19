package mcjty.lib.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

/**
 * Use this in case you want a container with no slots (for example, for energy storage only).
 */
public class EmptyContainer extends GenericContainer {

    // @todo REMOVE ME
    public EmptyContainer(PlayerEntity player, IInventory inventory) {
        super(null, 0, EmptyContainerFactory.getInstance(), BlockPos.ZERO);
    }

    public EmptyContainer(PlayerEntity player) {
        this(player, null);
    }

    @Override
    public void putStackInSlot(int index, ItemStack stack) {
    }
}
