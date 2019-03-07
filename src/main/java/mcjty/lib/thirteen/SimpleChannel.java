package mcjty.lib.thirteen;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class SimpleChannel {

    private final SimpleNetworkWrapper network;

    public SimpleChannel(String name) {
        this.network = NetworkRegistry.INSTANCE.newSimpleChannel(name);
    }

    public SimpleNetworkWrapper getNetwork() {
        return network;
    }

    public <MSG extends IMessage> void registerMessageServer(int index, Class<MSG> messageType, BiConsumer<MSG, ByteBuf> encoder, Function<ByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<Context>> messageConsumer) {
        network.registerMessage((message, ctx) -> {
            messageConsumer.accept(message, () -> new Context(ctx));
            return null;
        }, messageType, index, Side.SERVER);
    }

    public <MSG extends IMessage> void registerMessageClient(int index, Class<MSG> messageType, BiConsumer<MSG, ByteBuf> encoder, Function<ByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<Context>> messageConsumer) {
        network.registerMessage((message, ctx) -> {
            messageConsumer.accept(message, () -> new Context(ctx));
            return null;
        }, messageType, index, Side.CLIENT);
    }
}
