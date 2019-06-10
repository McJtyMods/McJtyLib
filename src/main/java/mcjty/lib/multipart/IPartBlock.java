package mcjty.lib.multipart;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public interface IPartBlock {

    @Nonnull
    PartSlot getSlotFromState(World world, BlockPos pos, BlockState newState);
}
