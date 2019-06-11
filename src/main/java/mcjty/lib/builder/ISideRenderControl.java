package mcjty.lib.builder;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface ISideRenderControl {
    boolean doesSideBlockRendering(BlockState state, IBlockReader world, BlockPos pos, Direction face);
}
