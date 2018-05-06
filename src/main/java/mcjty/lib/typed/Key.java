package mcjty.lib.typed;

import javax.annotation.Nonnull;

/**
 * A key in a TypedMap
 */
public final class Key<T> {

    @Nonnull private final String name;
    @Nonnull private final Type<T> type;

    public Key(@Nonnull String name, @Nonnull Type<T> type) {
        this.name = name;
        this.type = type;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public Type<T> getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Key)) return false;

        Key<?> key = (Key<?>) o;

        if (!name.equals(key.name)) return false;
        if (!type.equals(key.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
