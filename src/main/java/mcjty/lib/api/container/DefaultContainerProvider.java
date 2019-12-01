package mcjty.lib.api.container;

import mcjty.lib.tileentity.GenericEnergyStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class DefaultContainerProvider<C extends IGenericContainer> implements INamedContainerProvider {

    private final String name;
    private BiFunction<Integer, PlayerEntity, C> containerSupplier;
    private Supplier<? extends IItemHandler> itemHandler = () -> null;
    private Supplier<? extends GenericEnergyStorage> energyHandler = () -> null;
    private List<IntReferenceHolder> integerListeners = new ArrayList<>();
    private List<IntReferenceHolder> shortListeners = new ArrayList<>();

    public DefaultContainerProvider(String name) {
        this.name = name;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(name);
    }

    public DefaultContainerProvider<C> containerSupplier(BiFunction<Integer, PlayerEntity, C> containerSupplier) {
        this.containerSupplier = containerSupplier;
        return this;
    }

    public DefaultContainerProvider<C> itemHandler(LazyOptional<? extends IItemHandler> itemHandler) {
        this.itemHandler = () -> itemHandler.map(h -> h).orElseThrow(RuntimeException::new);
        return this;
    }

    public DefaultContainerProvider<C> itemHandler(Supplier<? extends IItemHandler> itemHandler) {
        this.itemHandler = itemHandler;
        return this;
    }

    public DefaultContainerProvider<C> energyHandler(LazyOptional<? extends GenericEnergyStorage> energyHandler) {
        this.energyHandler = () -> energyHandler.map(h -> h).orElseThrow(RuntimeException::new);
        return this;
    }

    public DefaultContainerProvider<C> energyHandler(Supplier<? extends GenericEnergyStorage> energyHandler) {
        this.energyHandler = energyHandler;
        return this;
    }

    public DefaultContainerProvider<C> integerListener(IntReferenceHolder holder) {
        integerListeners.add(holder);
        return this;
    }

    public DefaultContainerProvider<C> shortListener(IntReferenceHolder holder) {
        shortListeners.add(holder);
        return this;
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        C container = containerSupplier.apply(windowId, playerEntity);
        IItemHandler itemHandler = this.itemHandler.get();
        container.setupInventories(itemHandler, playerInventory);
        GenericEnergyStorage energyHandler = this.energyHandler.get();
        if (energyHandler != null) {
            energyHandler.addIntegerListeners(container);
        }
        for (IntReferenceHolder listener : integerListeners) {
            container.addIntegerListener(listener);
        }
        for (IntReferenceHolder listener : shortListeners) {
            container.addShortListener(listener);
        }

        return container.getAsContainer();
    }
}
