package mcjty.lib.builder;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface IGetBoundingBox {
    AxisAlignedBB getBoundingBox(BlockState state, IBlockReader source, BlockPos pos);
}
