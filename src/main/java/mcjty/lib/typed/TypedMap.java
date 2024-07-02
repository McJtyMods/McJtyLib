package mcjty.lib.typed;

import mcjty.lib.network.TypedMapTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public record TypedMap(Map<Key<?>, Object> map) {

    public static final TypedMap EMPTY = TypedMap.builder().build();

    public static final StreamCodec<RegistryFriendlyByteBuf, TypedMap> STREAM_CODEC = StreamCodec.of(
            TypedMapTools::writeArguments,
            TypedMapTools::readArguments);

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
}
