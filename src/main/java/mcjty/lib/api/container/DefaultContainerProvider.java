package mcjty.lib.api.container;

import mcjty.lib.tileentity.GenericEnergyStorage;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class DefaultContainerProvider<C extends IGenericContainer> implements MenuProvider {

    private final String name;
    private BiFunction<Integer, Player, C> containerSupplier;
    private Supplier<? extends IItemHandler> itemHandler = () -> null;
    private Supplier<? extends GenericEnergyStorage> energyHandler = () -> null;
    private final List<DataSlot> integerListeners = new ArrayList<>();
    private final List<DataSlot> shortListeners = new ArrayList<>();
    private final List<IContainerDataListener> containerDataListeners = new ArrayList<>();

    public DefaultContainerProvider(String name) {
        this.name = name;
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent(name);
    }

    public DefaultContainerProvider<C> containerSupplier(BiFunction<Integer, Player, C> containerSupplier) {
        this.containerSupplier = containerSupplier;
        return this;
    }

    public DefaultContainerProvider<C> itemHandler(Supplier<? extends IItemHandler> itemHandler) {
        this.itemHandler = itemHandler;
        return this;
    }

    public DefaultContainerProvider<C> energyHandler(Supplier<? extends GenericEnergyStorage> energyHandler) {
        this.energyHandler = energyHandler;
        return this;
    }

    public DefaultContainerProvider<C> dataListener(IContainerDataListener dataListener) {
        this.containerDataListeners.add(dataListener);
        return this;
    }

    public DefaultContainerProvider<C> integerListener(DataSlot holder) {
        integerListeners.add(holder);
        return this;
    }

    public DefaultContainerProvider<C> shortListener(DataSlot holder) {
        shortListeners.add(holder);
        return this;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
        C container = containerSupplier.apply(windowId, playerEntity);
        IItemHandler itemHandler = this.itemHandler.get();
        container.setupInventories(itemHandler, playerInventory);
        GenericEnergyStorage energyHandler = this.energyHandler.get();
        if (energyHandler != null) {
            energyHandler.addIntegerListeners(container);
        }
        for (DataSlot listener : integerListeners) {
            container.addIntegerListener(listener);
        }
        for (DataSlot listener : shortListeners) {
            container.addShortListener(listener);
        }
        for (IContainerDataListener dataListener : containerDataListeners) {
            container.addContainerDataListener(dataListener);
        }

        return container.getAsContainer();
    }
}
