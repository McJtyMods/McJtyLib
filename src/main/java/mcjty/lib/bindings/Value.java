package mcjty.lib.bindings;

import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.varia.NamedEnum;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Declare this as a static field in your GenericTileEntity and annotate with @Val to make this
 * a client <-> gui synced value
 */
public record Value<T extends GenericTileEntity, V>(Key<V> key,
                                                    Function<T, V> supplier,
                                                    BiConsumer<T, V> consumer) {

    /// Create a value with a getter and setter
    public static <TT extends GenericTileEntity, VV> Value<TT, VV> create(String name, Type<VV> type, Function<TT, VV> supplier, BiConsumer<TT, VV> consumer) {
        return new Value<>(new Key<>(name, type), supplier, consumer);
    }

    /// Create an enum value which uses a string and the name of the enum (NamedEnum)
    public static <TT extends GenericTileEntity, E extends NamedEnum<E>> Value<TT, String> createEnum(String name, E[] values, Function<TT, E> supplier, BiConsumer<TT, E> consumer) {
        return create(name, Type.STRING,
                te -> supplier.apply(te).getName(),
                (te, v) -> consumer.accept(te, NamedEnum.getEnumByName(v, values)));

    }
}
