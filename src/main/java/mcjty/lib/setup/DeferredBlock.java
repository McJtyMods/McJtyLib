package mcjty.lib.setup;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public final class DeferredBlock<T extends Block> extends DeferredHolder<Block, T> {

    DeferredBlock(ResourceLocation id) {
        super(id);
    }
}
