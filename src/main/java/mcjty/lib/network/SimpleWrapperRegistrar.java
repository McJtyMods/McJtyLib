package mcjty.lib.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class SimpleWrapperRegistrar implements IPayloadRegistrar {

    private final String modid;
    private SimpleChannel channel = null;
    private String version = "1.0";
    private int packetId = 0;

    public SimpleWrapperRegistrar(String modid) {
        this.modid = modid;
    }

    @Override
    public IPayloadRegistrar versioned(String version) {
        this.version = version;
        return this;
    }

    @Override
    public IPayloadRegistrar optional() {
        return this;
    }

    private int id() {
        return packetId++;
    }

    @Override
    public <T extends CustomPacketPayload> void play(Class<T> clazz, Function<FriendlyByteBuf, T> create, Consumer<IHandlerGetter<T>> handler) {
        handler.accept(new IHandlerGetter<T>() {
            @Override
            public void client(BiConsumer<T, PlayPayloadContext> handle) {
                channel.registerMessage(id(), clazz, (t, buf) -> ((T)t).write(buf), create, PlayPayloadContext.wrap(handle));
            }

            @Override
            public void server(BiConsumer<T, PlayPayloadContext> handle) {
                channel.registerMessage(id(), clazz, (t, buf) -> ((T)t).write(buf), create, PlayPayloadContext.wrap(handle));
            }
        });
    }

    public interface IHandlerGetter<T extends CustomPacketPayload> {
        void client(BiConsumer<T, PlayPayloadContext> handle);
        void server(BiConsumer<T, PlayPayloadContext> handle);
    }

    @Override
    public SimpleChannel getChannel() {
        if (channel == null) {
            channel = NetworkRegistry.ChannelBuilder
                    .named(new ResourceLocation(modid, modid))
                    .networkProtocolVersion(() -> version)
                    .clientAcceptedVersions(s -> true)
                    .serverAcceptedVersions(s -> true)
                    .simpleChannel();
        }
        return channel;
    }
}
