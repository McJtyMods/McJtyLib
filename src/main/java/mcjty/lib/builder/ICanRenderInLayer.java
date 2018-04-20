package mcjty.lib.builder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;

public interface ICanRenderInLayer {
    boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer);
}
