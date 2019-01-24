package mcjty.lib.multipart;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public interface IPartBlock {

    @Nonnull
    PartSlot getSlotForPlacement(World world, BlockPos pos, IBlockState newState);
}
