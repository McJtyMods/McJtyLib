package mcjty.lib.setup;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.RegistryObject;

import java.util.function.Supplier;

public class DeferredBlock<T extends Block> implements Supplier<T> {

    private final RegistryObject<T> supplier;
    private T block = null;

    public DeferredBlock(RegistryObject<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (block == null) {
            block = supplier.get();
        }
        return block;
    }

    public ResourceLocation getId() {
        return supplier.getId();
    }
}
