package mcjty.lib.builder;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public interface IGetLightValue {
    int getLightValue(BlockState state, IBlockReader world, BlockPos pos);
}
