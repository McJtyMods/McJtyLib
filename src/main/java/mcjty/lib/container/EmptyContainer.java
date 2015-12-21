package mcjty.lib.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Use this in case you want a container with no slots (for example, for energy storage only).
 */
public class EmptyContainer extends GenericContainer {

    public EmptyContainer(EntityPlayer player, IInventory inventory) {
        super(EmptyContainerFactory.getInstance());
    }

    public EmptyContainer(EntityPlayer player) {
        this(player, null);
    }

    @Override
    public void putStackInSlot(int index, ItemStack stack) {
    }
}
