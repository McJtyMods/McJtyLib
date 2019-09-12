package mcjty.lib.api.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.items.IItemHandler;

public interface IGenericContainer {

    void addIntegerListener(IntReferenceHolder holder);

    void setupInventories(IItemHandler itemHandler, PlayerInventory inventory);

    Container getAsContainer();
}
