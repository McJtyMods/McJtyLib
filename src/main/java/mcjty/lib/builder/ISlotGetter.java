package mcjty.lib.builder;

import mcjty.lib.multipart.PartSlot;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISlotGetter {

    PartSlot getSlotFromState(World world, BlockPos pos, IBlockState newState);
}
