package mcjty.lib.varia;

public interface TriConsumer<K, V, S> {
    void accept(K k, V v, S s);
}
