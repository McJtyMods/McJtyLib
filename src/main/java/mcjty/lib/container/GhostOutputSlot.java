package mcjty.lib.container;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;
import mcjty.lib.varia.InventoryTools;

import javax.annotation.Nonnull;

public class GhostOutputSlot extends ResourceHandlerSlot {

    public GhostOutputSlot(ResourceHandler<ItemResource> inventory, int index, int x, int y) {
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
        return 64;
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return false;
    }
}
