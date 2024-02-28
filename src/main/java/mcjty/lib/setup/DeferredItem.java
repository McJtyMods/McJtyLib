package mcjty.lib.setup;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.RegistryObject;

import java.util.function.Supplier;

public class DeferredItem<T extends Item> implements Supplier<T> {

    private final RegistryObject<T> supplier;
    private T item = null;

    public DeferredItem(RegistryObject<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (item == null) {
            item = supplier.get();
        }
        return item;
    }

    public ResourceLocation getId() {
        return supplier.getId();
    }
}
