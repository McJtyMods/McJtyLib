package mcjty.lib.api.container;

import mcjty.lib.container.GenericContainer;
import mcjty.lib.sync.AnnotationSyncScanner;
import mcjty.lib.sync.GuiSync;
import mcjty.lib.sync.SyncType;
import mcjty.lib.tileentity.GenericEnergyStorage;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.varia.Sync;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
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

    public DefaultContainerProvider<C> setupSync(GenericTileEntity te) {
        AnnotationSyncScanner.scanGuiSync(te.getClass(), te, (guiSync, field) -> {
            SyncType type = guiSync.type();
            if (type == SyncType.AUTOMATIC) {
                type = guessType(field);
            }
            switch (type) {
                case SHORT:
                    addSyncShortListener(te, field);
                    break;
                case INT:
                    addSyncIntegerListener(te, field);
                    break;
                case STRING:
                    break;
                case BOOL:
                    addSyncBoolListener(te, field);
                    break;
                case ENUM:
                    addSyncEnumListener(te, guiSync, field);
                    break;
            }
        });

        return this;
    }

    private SyncType guessType(Field field) {
        if (field.getType().isEnum()) {
            return SyncType.ENUM;
        }
        if (field.getType().isPrimitive()) {
            if (field.getType() == int.class || field.getType() == Integer.class) {
                return SyncType.INT;
            }
            if (field.getType() == short.class || field.getType() == Short.class) {
                return SyncType.SHORT;
            }
            if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                return SyncType.BOOL;
            }
        }
        if (field.getType() == String.class) {
            return SyncType.STRING;
        }
        throw new RuntimeException("Can't guess type for field " + field.getName() + "!");
    }

    private void addSyncEnumListener(GenericTileEntity te, GuiSync guiSync, Field field) {
        shortListener(Sync.enumeration(() -> {
            try {
                return (Enum) FieldUtils.readField(field, te, true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing field", e);
            }
        }, b -> {
            try {
                FieldUtils.writeField(field, te, b, true);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing field", e);
            }
        }, getEnumConstants(field.getType())));
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
        for (IContainerDataListener dataListener : containerDataListeners) {
            container.addContainerDataListener(dataListener);
        }

        if (container instanceof GenericContainer) {
            ((GenericContainer) container).forceBroadcast();
        }

        return container.getAsContainer();
    }
}
