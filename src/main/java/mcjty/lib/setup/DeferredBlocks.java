package mcjty.lib.setup;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

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
    public <T extends Block> DeferredBlock<T> register(String name, Function<BlockBehaviour.Properties, T> factory) {
        return register.registerBlock(name, factory);
    }

    public static DeferredBlocks create(String modid) {
        return new DeferredBlocks(modid);
    }
}
