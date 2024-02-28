package mcjty.lib.network;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.simple.SimpleChannel;

import java.util.function.Consumer;
import java.util.function.Function;

// For NeoForge porting
public interface IPayloadRegistrar {

    <T extends CustomPacketPayload> void play(Class<T> clazz, Function<FriendlyByteBuf, T> create, Consumer<SimpleWrapperRegistrar.IHandlerGetter<T>> handler);

    IPayloadRegistrar versioned(String version);

    IPayloadRegistrar optional();

    SimpleChannel getChannel();
}
