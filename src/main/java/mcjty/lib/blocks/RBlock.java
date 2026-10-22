package mcjty.lib.blocks;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import mcjty.lib.setup.DeferredBlock;
import mcjty.lib.setup.DeferredHolder;
import mcjty.lib.setup.DeferredItem;

public record RBlock<B extends Block, I extends BlockItem, E extends BlockEntity> (
        DeferredBlock<B> block,
        DeferredItem<I> item,
        DeferredHolder<BlockEntityType<?>, BlockEntityType<E>> be) {
}
