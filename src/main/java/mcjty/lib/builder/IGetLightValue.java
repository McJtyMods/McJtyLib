package mcjty.lib.builder;

import net.minecraft.block.state.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IGetLightValue {
    int getLightValue(BlockState state, IBlockAccess world, BlockPos pos);
}
