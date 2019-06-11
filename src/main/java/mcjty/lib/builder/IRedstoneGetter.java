package mcjty.lib.builder;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface IRedstoneGetter {
    int getRedstoneOutput(BlockState state, IBlockReader world, BlockPos pos, Direction side);
}
