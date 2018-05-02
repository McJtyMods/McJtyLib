package mcjty.lib.builder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IGetLightValue {
    int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos);
}
