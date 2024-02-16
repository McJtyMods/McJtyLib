package mcjty.lib.setup;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class DeferredBlocks {

    private final DeferredRegister<Block> register;
    private final String modid;

    private DeferredBlocks(String modid) {
        this.modid = modid;
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
