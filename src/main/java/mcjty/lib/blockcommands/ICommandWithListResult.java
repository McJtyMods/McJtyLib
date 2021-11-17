package mcjty.lib.blockcommands;

import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;
import java.util.List;

@FunctionalInterface
public interface ICommandWithListResult<TE extends GenericTileEntity, T> {
    @Nonnull
    List<T> run(TE te, PlayerEntity player, TypedMap params);
}
