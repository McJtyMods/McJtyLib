package mcjty.lib.blockcommands;

import net.minecraft.network.PacketBuffer;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Internal class used by McJtyLib to keep track of things needed for list commands
 */
public class CommandInfo<T> {
    private final Class<T> type;
    private final Function<PacketBuffer, T> deserializer;
    private final BiConsumer<PacketBuffer, T> serializer;

    public CommandInfo(Class<T> type, Function<PacketBuffer, T> deserializer, BiConsumer<PacketBuffer, T> serializer) {
        this.type = type;
        this.deserializer = deserializer;
        this.serializer = serializer;
    }

    public Class<T> getType() {
        return type;
    }

    public Function<PacketBuffer, T> getDeserializer() {
        return deserializer;
    }

    public BiConsumer<PacketBuffer, T> getSerializer() {
        return serializer;
    }
}
