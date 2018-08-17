package mcjty.lib.builder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

public interface IGetAIPathNodeType {

    /**
     * Return null to do the default handling
     */
    @Nullable
    PathNodeType getAiPathNodeType(IBlockState state, IBlockAccess world, BlockPos pos);
}
