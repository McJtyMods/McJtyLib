package mcjty.lib.api.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;

public interface IGenericContainer {

    void addShortListener(DataSlot holder);

    void addIntegerListener(DataSlot holder);

    void addContainerDataListener(IContainerDataListener dataListener);

    void setupInventories(@Nullable IItemHandler itemHandler, Inventory inventory);

    AbstractContainerMenu getAsContainer();
}
