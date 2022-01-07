package mcjty.lib.tileentity;

import mcjty.lib.typed.Key;

import java.util.function.BiConsumer;
import java.util.function.Function;

public record ValueHolder<T extends GenericTileEntity, V>(Key<V> key,
                                                          Function<T, V> getter,
                                                          BiConsumer<T, V> setter) {
}
