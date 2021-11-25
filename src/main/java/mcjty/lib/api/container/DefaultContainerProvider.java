package mcjty.lib.api.container;

import mcjty.lib.McJtyLib;
import mcjty.lib.container.ContainerFactory;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.Sync;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DefaultContainerProvider<C extends IGenericContainer> implements INamedContainerProvider {

    private final String name;
    private BiFunction<Integer, PlayerEntity, C> containerSupplier;
    private Supplier<? extends IItemHandler> itemHandler = () -> null;
    private Supplier<? extends GenericEnergyStorage> energyHandler = () -> null;
    private final List<IntReferenceHolder> integerListeners = new ArrayList<>();
    private final List<IntReferenceHolder> shortListeners = new ArrayList<>();
    private final List<IContainerDataListener> containerDataListeners = new ArrayList<>();

    /**
     * Conveniance method to make a supplier for an empty container (ContainerFactory.EMPTY).
     * Use this if you want to have a container for syncing values but otherwise have no items
     */
    public static Function<Integer, GenericContainer> empty(@Nonnull Supplier<ContainerType<GenericContainer>> type, GenericTileEntity te) {
        return windowId -> new GenericContainer(type, windowId, ContainerFactory.EMPTY, te);
    }

    public DefaultContainerProvider(String name) {
        this.name = name;
    }

    @Override
    @Nonnull
    public ITextComponent getDisplayName() {
        return new StringTextComponent(name);
    }

    public DefaultContainerProvider<C> containerSupplier(BiFunction<Integer, PlayerEntity, C> containerSupplier) {
        this.containerSupplier = containerSupplier;
        return this;
    }

    public DefaultContainerProvider<C> containerSupplier(Function<Integer, C> containerSupplier) {
        this.containerSupplier = (windowId, playerEntity) -> containerSupplier.apply(windowId);
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

    public DefaultContainerProvider<C> integerListener(IntReferenceHolder holder) {
        integerListeners.add(holder);
        return this;
    }

    public DefaultContainerProvider<C> shortListener(IntReferenceHolder holder) {
        shortListeners.add(holder);
        return this;
    }

    /**
     * Setup listeners to make sure that all fields annotated with @GuiValue
     * get properly propagated to the client when that client has this container open
     */
    public DefaultContainerProvider<C> setupSync(GenericTileEntity te) {
        dataListener(Sync.values(new ResourceLocation(McJtyLib.MODID, "data"), te));
        return this;
    }

    private void addSyncStringListener(GenericTileEntity te, AtomicInteger idx, Field field) {
        dataListener(Sync.string(new ResourceLocation(McJtyLib.MODID, "s" + idx.getAndIncrement()), () -> {
            try {
                return (String) FieldUtils.readField(field, te, true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing field", e);
            }
        }, s -> {
            try {
                FieldUtils.writeField(field, te, s, true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing field", e);
            }
        }));
    }

    private Enum[] getEnumConstants(Class clazz) {
        List<Enum> fields = Arrays.stream(clazz.getEnumConstants()).map(o -> (Enum) o).collect(Collectors.toList());
        return fields.toArray(new Enum[0]);
    }

    private void addSyncBoolListener(GenericTileEntity te, Field field) {
        shortListener(Sync.bool(() -> {
            try {
                return (boolean) FieldUtils.readField(field, te, true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing field", e);
            }
        }, b -> {
            try {
                FieldUtils.writeField(field, te, b, true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing field", e);
            }
        }));
    }

    private void addSyncIntegerListener(GenericTileEntity te, Field field) {
        integerListener(Sync.integer(() -> {
            try {
                return (int) FieldUtils.readField(field, te, true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing field", e);
            }
        }, integer -> {
            try {
                FieldUtils.writeField(field, te, integer, true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing field", e);
            }
        }));
    }

    private void addSyncShortListener(GenericTileEntity te, Field field) {
        shortListener(Sync.shortint(() -> {
            try {
                return (short) FieldUtils.readField(field, te, true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing field", e);
            }
        }, integer -> {
            try {
                FieldUtils.writeField(field, te, integer, true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing field", e);
            }
        }));
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
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
        for (IContainerDataListener dataListener : containerDataListeners) {
            container.addContainerDataListener(dataListener);
        }

        if (container instanceof GenericContainer) {
            ((GenericContainer) container).forceBroadcast();
        }

        return container.getAsContainer();
    }
}
