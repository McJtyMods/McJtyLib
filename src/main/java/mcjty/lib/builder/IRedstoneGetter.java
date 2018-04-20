package mcjty.lib.builder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IRedstoneGetter {
    int getRedstoneOutput(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side);
}
