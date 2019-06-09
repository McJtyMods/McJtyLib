package mcjty.lib.builder;

import net.minecraft.block.state.BlockState;
import net.minecraft.util.BlockRenderLayer;

public interface ICanRenderInLayer {
    boolean canRenderInLayer(BlockState state, BlockRenderLayer layer);
}
