package mcjty.lib.tileentity;

import mcjty.lib.typed.Key;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ValueHolder<T extends GenericTileEntity, V> {

    private final Key<V> key;
    private final Function<T, V> getter;
    private final BiConsumer<T, V> setter;

    public ValueHolder(Key<V> key, Function<T, V> getter, BiConsumer<T, V> setter) {
        this.key = key;
        this.getter = getter;
        this.setter = setter;
    }

    public Key<V> getKey() {
        return key;
    }

    public Function<T, V> getter() {
        return getter;
    }

    public BiConsumer<T, V> setter() {
        return setter;
    }
}
