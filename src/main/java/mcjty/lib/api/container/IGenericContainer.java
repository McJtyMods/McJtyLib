package mcjty.lib.api.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public interface IGenericContainer {

    void addShortListener(IntReferenceHolder holder);

    void addIntegerListener(IntReferenceHolder holder);

    void addContainerDataListener(IContainerDataListener dataListener);

    void setupInventories(@Nullable IItemHandler itemHandler, PlayerInventory inventory);

    Container getAsContainer();
}
