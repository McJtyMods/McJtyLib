package mcjty.lib.typed;

import javax.annotation.Nonnull;
import java.util.*;

public final class TypedMap {

    public static final TypedMap EMPTY = TypedMap.builder().build();
    private final Map<Key<?>, Object> map;

    TypedMap(Map<Key<?>, Object> map) {
        this.map = new HashMap<>(map);
    }

    public Set<Key<?>> getKeys() {
        return map.keySet();
    }

    public int size() {
        return map.size();
    }

    public <V> V get(@Nonnull Key<V> key) {
        return (V) map.get(key);
    }

    public <V> Optional<V> getOptional(@Nonnull Key<V> key) {
        return Optional.ofNullable((V) map.get(key));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<Key<?>, Object> map = new HashMap<>();

        Builder() {
        }

        public <V> Builder put(@Nonnull Key<V> key, V value) {
            map.put(key, value);
            return this;
        }

        public TypedMap build() {
            return new TypedMap(map);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypedMap typedMap = (TypedMap) o;
        return Objects.equals(map, typedMap.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(map);
    }
}
