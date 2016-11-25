package mcjty.lib.network;

import mcjty.typed.Type;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * Implement this interface if you want to receive server-side messages (typically sent from a packet that
 * implements PacketRequestListFromServer or PacketServerCommand).
 */
public interface CommandHandler {
    /// Return true if command was handled correctly. False if not.
    boolean execute(EntityPlayerMP playerMP, String command, Map<String, Argument> args);

    /// Return the result which will be sent back to the client
    @Nonnull
    <T> List<T> executeWithResultList(String command, Map<String, Argument> args, Type<T> type);

    /// Return a numeric result which will be sent back to the client. Returns null if command was not handled.
    Integer executeWithResultInteger(String command, Map<String, Argument> args);
}
