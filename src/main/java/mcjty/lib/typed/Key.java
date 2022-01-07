package mcjty.lib.typed;

import javax.annotation.Nonnull;

/**
 * A key in a TypedMap
 */
public record Key<T>(@Nonnull String name, @Nonnull Type<T> type) {
}
