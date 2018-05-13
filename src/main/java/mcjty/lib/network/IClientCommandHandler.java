package mcjty.lib.network;

import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Implement this interface if you want to receive client-side messages (typically sent from a packet that
 * implements PacketListFromServer).
 */
public interface IClientCommandHandler {

    /// Return true if command was handled correctly. False if not.
    <T> boolean receiveListFromServer(String command, List<T> list, Type<T> type);

    /// Return true if command was handled correctly. False if not.
    boolean receiveDataFromServer(String command, @Nonnull TypedMap result);
}
