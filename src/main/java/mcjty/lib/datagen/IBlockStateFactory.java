package mcjty.lib.datagen;

import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public interface IBlockStateFactory {

    void simple(Supplier<? extends Block> blockSupplier, BaseBlockStateProvider provider);
}
