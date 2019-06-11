package mcjty.lib.builder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public interface IActivateAction {

    /**
     * Return false to let the default activation handling work
     */
    boolean doActivate(World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result);
}
