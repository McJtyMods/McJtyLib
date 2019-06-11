package mcjty.lib.builder;

import mcjty.lib.multipart.PartSlot;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISlotGetter {

    PartSlot getSlotFromState(World world, BlockPos pos, BlockState newState);
}
