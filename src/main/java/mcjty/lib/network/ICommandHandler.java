package mcjty.lib.network;

import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Implement this interface if you want to receive server-side messages (typically sent from a packet that
 * implements PacketRequestListFromServer or PacketServerCommand).
 */
public interface ICommandHandler {

    /// Return the result which will be sent back to the client
    @Nonnull
    <T> List<T> executeWithResultList(String command, TypedMap args, Type<T> type);
}
