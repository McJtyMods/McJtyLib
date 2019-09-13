package mcjty.lib.api.smartwrench;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public interface ISmartWrenchSelector {

    /**
     * This is only called server side. Select a block for this tile entity.
     */
    void selectBlock(PlayerEntity player, BlockPos pos);
}
