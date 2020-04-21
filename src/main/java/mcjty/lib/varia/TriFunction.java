package mcjty.lib.varia;

public interface TriFunction<K, V, S, R> {
    R apply(K k, V v, S s);
}
