package mcjty.lib.setup;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class DeferredItems {

    private final DeferredRegister<Item> register;

    private DeferredItems(String modid) {
        register = DeferredRegister.create(ForgeRegistries.ITEMS, modid);
    }

    public void register(IEventBus bus) {
        register.register(bus);
    }

    public <T extends Item> DeferredItem<T> register(String name, Supplier<T> supplier) {
        return new DeferredItem<>(register.register(name, supplier));
    }

    public static DeferredItems create(String modid) {
        return new DeferredItems(modid);
    }
}
