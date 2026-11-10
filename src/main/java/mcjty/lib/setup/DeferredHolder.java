package mcjty.lib.setup;

import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

/** A lazy reference bound when its owning register is initialized. */
public class DeferredHolder<R, T extends R> implements Supplier<T> {

    private final ResourceLocation id;
    private T value;

    protected DeferredHolder(ResourceLocation id) {
        this.id = id;
    }

    void bind(T value) {
        if (this.value != null) throw new IllegalStateException("Already registered: " + id);
        this.value = value;
    }

    public T get() {
        if (value == null) throw new IllegalStateException("Not registered: " + id);
        return value;
    }

    public ResourceLocation getId() {
        return id;
    }
}
