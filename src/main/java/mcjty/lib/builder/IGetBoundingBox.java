package mcjty.lib.builder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface IGetBoundingBox {
    AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos);
}
