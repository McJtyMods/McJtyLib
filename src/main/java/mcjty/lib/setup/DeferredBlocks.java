package mcjty.lib.setup;

import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class DeferredBlocks {

    private final DeferredRegister.Blocks register;

    private DeferredBlocks(String modid) {
        register = DeferredRegister.createBlocks(modid);
    }

    public void register(IEventBus bus) {
        register.register(bus);
    }

    public <T extends Block> DeferredBlock<T> register(String name, Supplier<T> supplier) {
        return register.register(name, supplier);
    }

    public static DeferredBlocks create(String modid) {
        return new DeferredBlocks(modid);
    }
}
