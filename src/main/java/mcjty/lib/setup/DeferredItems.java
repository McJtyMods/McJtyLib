package mcjty.lib.setup;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;

public class DeferredItems {

    private final DeferredRegister.Items register;

    private DeferredItems(String modid) {
        register = DeferredRegister.createItems(modid);
    }

    public void register() {
        register.register();
    }

    public <T extends Item> DeferredItem<T> register(String name, Supplier<T> supplier) {
        return register.register(name, supplier);
    }

    /** Register with properties whose registry id has been set by McJtyLib. */
    public <T extends Item> DeferredItem<T> register(String name, Function<Item.Properties, T> factory) {
        return register.registerItem(name, factory);
    }

    public static DeferredItems create(String modid) {
        return new DeferredItems(modid);
    }

    public DeferredRegister.Items getRegister() {
        return register;
    }
}
