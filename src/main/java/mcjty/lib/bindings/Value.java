package mcjty.lib.bindings;

import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Value<T extends GenericTileEntity, V> {

    private final Key<V> key;
    private final Function<T, V> supplier;
    private final BiConsumer<T, V> consumer;

    private Value(Builder<T, V> builder) {
        this.key = builder.key;
        this.supplier = builder.supplier;
        this.consumer = builder.consumer;
    }

    public Key<V> getKey() {
        return key;
    }

    public Function<T, V> getSupplier() {
        return supplier;
    }

    public BiConsumer<T, V> getConsumer() {
        return consumer;
    }

    public static <TT extends GenericTileEntity, VV> Builder<TT, VV> builder(String name, Type<VV> type) {
        return new Builder<TT, VV>(name, type);
    }

    /// Create a command without a result
    public static <TT extends GenericTileEntity, VV> Value<TT, VV> create(String name, Type<VV> type, Function<TT, VV> supplier, BiConsumer<TT, VV> consumer) {
        return new Builder<TT, VV>(name, type)
                .supplier(supplier)
                .consumer(consumer)
                .build();
    }

    public static class Builder<T extends GenericTileEntity, V> {

        private final Key<V> key;
        private Function<T, V> supplier;
        private BiConsumer<T, V> consumer;

        public Builder(String name, Type<V> type) {
            this.key = new Key<>(name, type);
        }

        public Builder<T, V> supplier(Function<T, V> supplier) {
            this.supplier = supplier;
            return this;
        }

        public Builder<T, V> consumer(BiConsumer<T, V> consumer) {
            this.consumer = consumer;
            return this;
        }

        public Value<T, V> build() {
            return new Value<T, V>(this);
        }
    }
}
