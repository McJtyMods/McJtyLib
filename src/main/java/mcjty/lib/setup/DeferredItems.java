package mcjty.lib.setup;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class DeferredItems {

    private final DeferredRegister.Items register;

    private DeferredItems(String modid) {
        register = DeferredRegister.createItems(modid);
    }

    public void register(IEventBus bus) {
        register.register(bus);
    }

    public <T extends Item> DeferredItem<T> register(String name, Supplier<T> supplier) {
        return register.register(name, supplier);
    }

    public static DeferredItems create(String modid) {
        return new DeferredItems(modid);
    }

    public DeferredRegister.Items getRegister() {
        return register;
    }
}
