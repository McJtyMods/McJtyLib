package mcjty.lib.blockcommands;

import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;

@FunctionalInterface
public interface ICommandWithList<TE extends GenericTileEntity, T> {
    void run(TE te, PlayerEntity player, TypedMap params, List<T> list);
}
