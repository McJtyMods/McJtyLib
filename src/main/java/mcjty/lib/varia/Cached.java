package mcjty.lib.varia;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * A value that has a value that is calculated lazily but that can also
 * be reset
 */
public interface Cached<T> extends Supplier<T> {

    /**
     * Constructs a lazy-initialized object
     */
    static <T> Cached<T> of(@Nonnull Supplier<T> supplier) {
        return new CachedImplementation<>(supplier);
    }

    /**
     * Clear the cached value
     */
    void clear();

    final class CachedImplementation<T> implements Cached<T> {
        private final Supplier<T> supplier;
        private T instance;

        private CachedImplementation(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Nullable
        @Override
        public final T get() {
            if (instance == null) {
                instance = supplier.get();
            }
            return instance;
        }

        @Override
        public void clear() {
            instance = null;
        }
    }
}