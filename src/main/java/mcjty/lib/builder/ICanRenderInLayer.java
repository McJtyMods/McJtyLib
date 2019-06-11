package mcjty.lib.builder;

import net.minecraft.block.BlockState;
import net.minecraft.util.BlockRenderLayer;

public interface ICanRenderInLayer {
    boolean canRenderInLayer(BlockState state, BlockRenderLayer layer);
}
