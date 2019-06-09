package mcjty.lib.builder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IClickAction {

    void doClick(World world, BlockPos pos, PlayerEntity player);
}
