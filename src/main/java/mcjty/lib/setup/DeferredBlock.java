package mcjty.lib.setup;

import net.minecraft.world.level.block.Block;
import net.minecraft.resources.Identifier;

public final class DeferredBlock<T extends Block> extends DeferredHolder<Block,T> {

    DeferredBlock(Identifier id) {
        super(id);
    }

}
