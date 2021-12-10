package mcjty.lib.blockcommands;

import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface IRunnableWithResult<TE extends GenericTileEntity> {
    @Nonnull TypedMap run(TE te, Player player, TypedMap params);
}
