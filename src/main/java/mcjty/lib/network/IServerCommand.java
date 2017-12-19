package mcjty.lib.network;

import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nonnull;

public interface IServerCommand {

    /**
     * Execute a command on the server through networking from a client
     * Returns false on failure
     */
    boolean execute(@Nonnull EntityPlayer player, @Nonnull Arguments arguments);
}
