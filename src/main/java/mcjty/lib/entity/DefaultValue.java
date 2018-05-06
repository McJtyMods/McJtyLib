package mcjty.lib.entity;

import mcjty.lib.typed.Key;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class DefaultValue<V, T extends GenericTileEntity> implements IValue<V, T> {

    private final Key<V> key;
    private final Function<T, V> getter;
    private final BiConsumer<T, V> setter;

    public DefaultValue(Key<V> key, Function<T, V> getter, BiConsumer<T, V> setter) {
        this.key = key;
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public Key<V> getKey() {
        return key;
    }

    @Override
    public Function<T, V> getter() {
        return getter;
    }

    @Override
    public BiConsumer<T, V> setter() {
        return setter;
    }
}
