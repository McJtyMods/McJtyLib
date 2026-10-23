package mcjty.lib.container;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import mcjty.lib.varia.InventoryTools;

import javax.annotation.Nonnull;

/**
 * A slot typically used for crafting grids.
 */
public class GhostSlot extends ResourceHandlerSlot {

    public GhostSlot(ResourceHandler<ItemResource> inventory, int index, int x, int y) {
        super(inventory, InventoryTools.modifier(inventory), index, x, y);
    }

    @Override
    public boolean mayPickup(Player player) {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack remove(int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getMaxStackSize() {
        return 0;
    }

    @Override
    public int getMaxStackSize(@Nonnull ItemStack stack) {
        return 1;
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return true;
    }

    @Override
    protected void setStackCopy(ItemStack stack) {
        if (!stack.isEmpty()) {
            stack.setCount(1);
        }
        super.setStackCopy(stack);
    }
}
