package mcjty.lib.varia;

import java.util.function.Supplier;

public final class Lazy<T> implements Supplier<T> {

    private Supplier<T> factory;
    private T value;

    private Lazy(Supplier<T> factory) {
        this.factory=factory;
    }

    public static <T> Lazy<T> of(Supplier<T> factory) {
        return new Lazy<>(factory);
    }

    public synchronized T get() {
        if(factory!=null) {
            value=factory.get();
            factory=null;
        }
        return value;
    }

}
