package mcjty.lib.setup;

import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.eventbus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class DeferredBlocks {

    private final DeferredRegister<Block> register;

    private DeferredBlocks(String modid) {
        register = DeferredRegister.create(ForgeRegistries.BLOCKS, modid);
    }

    public void register(IEventBus bus) {
        register.register(bus);
    }

    public <T extends Block> DeferredBlock<T> register(String name, Supplier<T> supplier) {
        return new DeferredBlock<>(register.register(name, supplier));
    }

    public static DeferredBlocks create(String modid) {
        return new DeferredBlocks(modid);
    }
}
