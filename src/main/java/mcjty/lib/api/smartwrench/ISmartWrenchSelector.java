package mcjty.lib.api.smartwrench;

import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;

public interface ISmartWrenchSelector {

    /**
     * This is only called server side. Select a block for this be entity.
     */
    void selectBlock(Player player, BlockPos pos);
}
