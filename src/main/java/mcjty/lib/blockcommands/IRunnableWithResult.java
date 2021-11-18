package mcjty.lib.blockcommands;

import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface IRunnableWithResult<TE extends GenericTileEntity> {
    @Nonnull TypedMap run(TE te, PlayerEntity player, TypedMap params);
}
