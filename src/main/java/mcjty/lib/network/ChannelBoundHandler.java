package mcjty.lib.network;

import mcjty.lib.varia.TriConsumer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ChannelBoundHandler<T> implements BiConsumer<T, Supplier<NetworkEvent.Context>> {
    private final SimpleChannel channel;
    private final TriConsumer<T, SimpleChannel, Supplier<NetworkEvent.Context>> innerHandler;

    public ChannelBoundHandler(SimpleChannel channel, TriConsumer<T, SimpleChannel, Supplier<NetworkEvent.Context>> innerHandler) {
        this.channel = channel;
        this.innerHandler = innerHandler;
    }

    @Override
    public void accept(T message, Supplier<NetworkEvent.Context> ctx) {
        innerHandler.accept(message, channel, ctx);
    }

}