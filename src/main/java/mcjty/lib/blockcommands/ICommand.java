package mcjty.lib.blockcommands;

import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import net.minecraft.entity.player.PlayerEntity;

@FunctionalInterface
public interface ICommand<T extends GenericTileEntity> {
    void run(T te, PlayerEntity player, TypedMap params);
}
