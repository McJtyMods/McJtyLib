package mcjty.lib.network;

import mcjty.lib.typed.TypedMap;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nonnull;

public interface IServerCommand {

    /**
     * Execute a command on the server through networking from a client
     * Returns false on failure
     */
    boolean execute(@Nonnull EntityPlayer player, @Nonnull TypedMap arguments);
}
