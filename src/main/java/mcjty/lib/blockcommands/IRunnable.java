package mcjty.lib.blockcommands;

import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface IRunnable<TE extends GenericTileEntity> {
    void run(TE te, Player player, TypedMap params);
}
