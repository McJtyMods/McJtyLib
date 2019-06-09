package mcjty.lib.network;

import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Implement this interface if you want to receive server-side messages (typically sent from a packet that
 * implements PacketRequestListFromServer or PacketServerCommand).
 */
public interface ICommandHandler {
    /// Return true if command was handled correctly. False if not.
    default boolean execute(PlayerEntity playerMP, String command, TypedMap params) {
        return false;
    }

    /// Return the result which will be sent back to the client
    @Nonnull
    <T> List<T> executeWithResultList(String command, TypedMap args, Type<T> type);

    /// Return a typed map result which will be sent back to the client. Returns null if command was not handled.
    @Nullable
    TypedMap executeWithResult(String command, TypedMap args);
}
