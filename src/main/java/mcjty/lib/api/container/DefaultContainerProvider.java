package mcjty.lib.api.container;

import mcjty.lib.tileentity.GenericEnergyStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

public class DefaultContainerProvider<C extends IGenericContainer> implements INamedContainerProvider {

    private final String name;
    private BiFunction<Integer, PlayerEntity, C> containerSupplier;
    private LazyOptional<? extends IItemHandler> itemHandler = LazyOptional.empty();
    private LazyOptional<? extends GenericEnergyStorage> energyHandler = LazyOptional.empty();

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
        this.itemHandler = itemHandler;
        return this;
    }

    public DefaultContainerProvider<C> energyHandler(LazyOptional<? extends GenericEnergyStorage> energyHandler) {
        this.energyHandler = energyHandler;
        return this;
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        C container = containerSupplier.apply(windowId, playerEntity);
        itemHandler.ifPresent(h -> container.setupInventories(h, playerInventory));
        energyHandler.ifPresent(e -> e.addIntegerListeners(container));
        return container.getAsContainer();
    }
}
