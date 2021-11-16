package mcjty.lib.blockcommands;

import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface ICommandWithResult<T extends GenericTileEntity> {
    @Nonnull TypedMap run(T te, PlayerEntity player, TypedMap params);
}
