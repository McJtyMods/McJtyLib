package mcjty.lib.bindings;

import mcjty.lib.typed.Key;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IValue<V> {

    Key<V> getKey();

    Supplier<V> getter();

    Consumer<V> setter();
}
