package mcjty.lib.datagen;

import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public interface IItemFactory {

    void parented(Supplier<? extends Block> blockSupplier, String model);

    void parented(Supplier<? extends Block> blockSupplier);
}
