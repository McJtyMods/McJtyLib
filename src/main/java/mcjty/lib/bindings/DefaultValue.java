package mcjty.lib.bindings;

import mcjty.lib.typed.Key;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class DefaultValue<V> implements IValue<V> {

    private final Key<V> key;
    private final Supplier<V> getter;
    private final Consumer<V> setter;

    public DefaultValue(Key<V> key, Supplier<V> getter, Consumer<V> setter) {
        this.key = key;
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public Key<V> getKey() {
        return key;
    }

    @Override
    public Supplier<V> getter() {
        return getter;
    }

    @Override
    public Consumer<V> setter() {
        return setter;
    }
}
