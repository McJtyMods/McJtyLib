package mcjty.lib.setup;

import net.minecraft.world.level.block.Block;
import mcjty.lib.setup.DeferredBlock;
import mcjty.lib.setup.DeferredRegister;
import java.util.function.Supplier;

public class DeferredBlocks {

    private final DeferredRegister.Blocks register;

    private DeferredBlocks(String modid) {
        register = DeferredRegister.createBlocks(modid);
    }

    public void register() {
        register.register();
    }

    public <T extends Block> DeferredBlock<T> register(String name, Supplier<T> supplier) {
        return register.register(name, supplier);
    }

    /** Register with properties whose registry id has been set by McJtyLib. */
    public <T extends Block> DeferredBlock<T> register(String name, java.util.function.Function<net.minecraft.world.level.block.state.BlockBehaviour.Properties, T> factory) {
        return register.registerBlock(name, factory);
    }

    public static DeferredBlocks create(String modid) {
        return new DeferredBlocks(modid);
    }
}
