package mcjty.lib.builder;

import net.minecraft.block.state.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IRedstoneGetter {
    int getRedstoneOutput(BlockState state, IBlockAccess world, BlockPos pos, Direction side);
}
