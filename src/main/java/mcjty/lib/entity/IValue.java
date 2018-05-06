package mcjty.lib.entity;

import mcjty.lib.typed.Key;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface IValue<V, T extends GenericTileEntity> {

    Key<V> getKey();

    Function<T, V> getter();

    BiConsumer<T, V> setter();
}
