package mcjty.lib.api.container;

import io.netty.buffer.ByteBuf;
import mcjty.lib.McJtyLib;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.ComponentFactory;
import mcjty.lib.varia.Sync;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final Map<AttachmentType<?>, StreamCodec<? extends ByteBuf, ?>> dataListeners = new HashMap<>();

    /**
     * Conveniance method to make a supplier for an empty container (ContainerFactory.EMPTY).
     * Use this if you want to have a container for syncing values but otherwise have no items
     */
    public static BiFunction<Integer, Player, GenericContainer> empty(@Nonnull Supplier<MenuType<GenericContainer>> type, GenericTileEntity te) {
        return (windowId, player) -> new GenericContainer(type, windowId, ContainerFactory.EMPTY, te, player);
    }

    /**
     * Conveniance method to make a supplier for a non-empty container
     */
    public static BiFunction<Integer, Player, GenericContainer> container(@Nonnull Supplier<MenuType<GenericContainer>> type, @Nonnull Supplier<ContainerFactory> factory, GenericTileEntity te) {
        return (windowId, player) -> new GenericContainer(type, windowId, factory, te, player);
    }

    public DefaultContainerProvider(String name) {
        this.name = name;
    }

    @Override
    @Nonnull
    public Component getDisplayName() {
        return ComponentFactory.literal(name);
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

    public <T> DefaultContainerProvider<C> data(Supplier<AttachmentType<T>> type, StreamCodec<? extends ByteBuf, T> codec) {
        dataListeners.put(type.get(), codec);
        return this;
    }

    /**
     * Setup listeners to make sure that all fields annotated with @GuiValue
     * get properly propagated to the client when that client has this container open
     */
    public DefaultContainerProvider<C> setupSync(GenericTileEntity te) {
        dataListener(Sync.values(ResourceLocation.fromNamespaceAndPath(McJtyLib.MODID, "data"), te));
        return this;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, @Nonnull Inventory playerInventory, @Nonnull Player playerEntity) {
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
        dataListeners.forEach(container::addDataListener);

        if (container instanceof GenericContainer) {
            ((GenericContainer) container).forceBroadcast();
        }

        return container.getAsContainer();
    }
}
