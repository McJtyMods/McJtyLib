package mcjty.lib.varia.codec;

@FunctionalInterface
public interface StreamDecoder<I, T> {
    T decode(I var1);
}
