package mcjty.lib.network;

import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Implement this interface if you want to receive server-side messages (typically sent from a packet that
 * implements PacketRequestListFromServer or PacketServerCommand).
 */
public interface ICommandHandler {
    /// Return true if command was handled correctly. False if not.
    default boolean execute(EntityPlayerMP playerMP, String command, TypedMap params) {
        return false;
    }

    /// Return the result which will be sent back to the client
    @Nonnull
    <T> List<T> executeWithResultList(String command, TypedMap args, Type<T> type);

    /// Return a numeric result which will be sent back to the client. Returns null if command was not handled.
    Integer executeWithResultInteger(String command, TypedMap args);
}
