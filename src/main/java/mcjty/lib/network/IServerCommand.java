package mcjty.lib.network;

import mcjty.lib.typed.TypedMap;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;

public interface IServerCommand {

    /**
     * Execute a command on the server through networking from a client
     * Returns false on failure
     */
    boolean execute(@Nonnull PlayerEntity player, @Nonnull TypedMap arguments);
}
