package mcjty.lib.blockcommands;

import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import java.util.List;

@FunctionalInterface
public interface IRunnableWithListResult<TE extends GenericTileEntity, T> {
    @Nonnull
    List<T> run(TE te, Player player, TypedMap params);
}
