package mcjty.lib.blockcommands;

import net.minecraft.network.FriendlyByteBuf;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Internal class used by McJtyLib to keep track of things needed for list commands
 */
public record CommandInfo<T>(Class<T> type, Function<FriendlyByteBuf, T> deserializer, BiConsumer<FriendlyByteBuf, T> serializer) {
}
