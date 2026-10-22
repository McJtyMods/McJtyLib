package mcjty.lib.setup;

import net.minecraft.resources.Identifier;
import java.util.function.Supplier;

/** A lazy reference bound when its owning register is initialized. */
public class DeferredHolder<R,T extends R> implements Supplier<T> {

    private final Identifier id;
    private T value;

    protected DeferredHolder(Identifier id) {
        this.id=id;
    }

    void bind(T value) {
        if(this.value!=null)throw new IllegalStateException("Already registered: "+id);
        this.value=value;
    }

    public T get() {
        if(value==null)throw new IllegalStateException("Not registered: "+id);
        return value;
    }

    public Identifier getId() {
        return id;
    }

}
