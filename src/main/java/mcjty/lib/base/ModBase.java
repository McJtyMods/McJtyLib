package mcjty.lib.base;

import mcjty.lib.network.Arguments;
import net.minecraft.entity.player.EntityPlayer;

public interface ModBase {
    String getModId();

    /**
     * Open the manual at a specific page.
     * @param player
     * @param bookindex
     * @param page
     */
    void openManual(EntityPlayer player, int bookindex, String page);

    /**
     * Handle a server command for a given position. This is a command as sent from the client by
     * PacketSendServerCommand
     */
    default void handleCommand(EntityPlayer player, String command, Arguments arguments) { }
}
