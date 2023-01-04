package mcjty.lib.datagen;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public interface ILootFactory {

    void simpleTable(Supplier<? extends Block> block);

    void standardTable(Supplier<? extends Block> block, Supplier<? extends BlockEntityType<?>> be);
}
