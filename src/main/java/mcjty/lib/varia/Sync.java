package mcjty.lib.varia;

import mcjty.lib.api.container.IContainerDataListener;
import mcjty.lib.tileentity.ValueHolder;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.Key;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Sync {

    /**
     * A data listener that listens to the values of the given tile entity. Use this
     * in a GenericContainer (using dataListener) to sync data for your gui.
     *
     * Note that DefaultContainerProvider.setupSync() already calls this
     */
    public static IContainerDataListener values(ResourceLocation id, GenericTileEntity te) {
        return new IContainerDataListener() {

            private Map<Key, Object> oldValues = new HashMap<>();

            @Override
            public ResourceLocation getId() {
                return id;
            }

            private void copyToOld() {
                oldValues.clear();
                for (ValueHolder value : te.getValueMap().values()) {
                    Object v = value.getter().apply(te);
                    oldValues.put(value.key(), v);
                }
            }

            @Override
            public boolean isDirtyAndClear() {
                for (ValueHolder value : te.getValueMap().values()) {
                    Object v = value.getter().apply(te);
                    Key<?> key = value.key();
                    if (!oldValues.containsKey(key) || !Objects.equals(oldValues.get(key), v)) {
                        // Dirty
                        copyToOld();
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void toBytes(FriendlyByteBuf buf) {
                for (ValueHolder value : te.getValueMap().values()) {
                    Object v = value.getter().apply(te);
                    value.key().type().serialize(buf, v);
                }
            }

            @Override
            public void readBuf(FriendlyByteBuf buf) {
                for (ValueHolder value : te.getValueMap().values()) {
                    value.key().type().deserialize(buf, value, te);
                }
            }
        };
    }

    /**
     * A data listener for a string. Use this
     * in a GenericContainer (using dataListener) to sync data for your gui
     */
    public static IContainerDataListener string(ResourceLocation id, Supplier<String> getter, Consumer<String> setter) {
        return new IContainerDataListener() {

            private String oldString = null;

            @Override
            public ResourceLocation getId() {
                return id;
            }

            @Override
            public boolean isDirtyAndClear() {
                String newValue = getter.get();
                if (!Objects.equals(newValue, oldString)) {
                    oldString = newValue;
                    return true;
                }
                return false;
            }

            @Override
            public void toBytes(FriendlyByteBuf buf) {
                buf.writeUtf(getter.get());
            }

            @Override
            public void readBuf(FriendlyByteBuf buf) {
                setter.accept(buf.readUtf(32767));
            }
        };
    }

    /**
     * A data listener for a float. Use this
     * in a GenericContainer (using dataListener) to sync data for your gui
     */
    public static IContainerDataListener flt(ResourceLocation id, Supplier<Float> getter, Consumer<Float> setter) {
        return new IContainerDataListener() {

            private Float oldFloat = null;

            @Override
            public ResourceLocation getId() {
                return id;
            }

            @Override
            public boolean isDirtyAndClear() {
                Float newValue = getter.get();
                if (!Objects.equals(newValue, oldFloat)) {
                    oldFloat = newValue;
                    return true;
                }
                return false;
            }

            @Override
            public void toBytes(FriendlyByteBuf buf) {
                buf.writeFloat(getter.get());
            }

            @Override
            public void readBuf(FriendlyByteBuf buf) {
                setter.accept(buf.readFloat());
            }
        };
    }

    public static DataSlot integer(Supplier<Integer> getter, Consumer<Integer> setter) {
        return new DataSlot() {
            @Override
            public int get() {
                return getter.get();
            }

            @Override
            public void set(int v) {
                setter.accept(v);
            }
        };
    }

    public static DataSlot shortint(Supplier<Short> getter, Consumer<Short> setter) {
        return new DataSlot() {
            @Override
            public int get() {
                return getter.get();
            }

            @Override
            public void set(int v) {
                setter.accept((short) v);
            }
        };
    }

    public static <T extends Enum<T>> DataSlot enumeration(Supplier<T> getter, Consumer<T> setter, T[] values) {
        return new DataSlot() {
            @Override
            public int get() {
                return getter.get().ordinal();
            }

            @Override
            public void set(int v) {
                setter.accept(values[v]);
            }
        };
    }

    public static DataSlot bool(Supplier<Boolean> getter, Consumer<Boolean> setter) {
        return new DataSlot() {
            @Override
            public int get() {
                return getter.get() ? 1 : 0;
            }

            @Override
            public void set(int v) {
                setter.accept(v != 0);
            }
        };
    }
}
