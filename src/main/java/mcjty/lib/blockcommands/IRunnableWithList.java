package mcjty.lib.blockcommands;

import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import net.minecraft.world.entity.player.Player;

import java.util.List;

@FunctionalInterface
public interface IRunnableWithList<TE extends GenericTileEntity, T> {
    void run(TE te, Player player, TypedMap params, List<T> list);
}
