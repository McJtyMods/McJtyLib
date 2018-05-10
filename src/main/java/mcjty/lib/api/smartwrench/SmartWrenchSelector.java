package mcjty.lib.api.smartwrench;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public interface SmartWrenchSelector {

    /**
     * This is only called server side. Select a block for this tile bindings.
     */
    void selectBlock(EntityPlayer player, BlockPos pos);
}
