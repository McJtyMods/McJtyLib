package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mcjty.lib.McJtyLib;
import mcjty.lib.api.container.IContainerDataListener;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.varia.SafeClientTools;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketContainerDataToClient(ResourceLocation containerId, RegistryFriendlyByteBuf buffer) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(McJtyLib.MODID, "containerdata");
    public static final CustomPacketPayload.Type<PacketContainerDataToClient> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketContainerDataToClient> CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeResourceLocation(packet.containerId);
                buf.writeBytes(packet.buffer().array());
            },
            buf -> {
                ResourceLocation containerId = buf.readResourceLocation();
                int l = buf.readInt();
                ByteBuf newbuf = Unpooled.buffer(l);
                byte[] bytes = new byte[l];
                buf.readBytes(bytes);
                newbuf.writeBytes(bytes);
                RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(newbuf, buf.registryAccess(), ConnectionType.OTHER);
                return new PacketContainerDataToClient(containerId, buffer);
            }
    );

    private static RegistryFriendlyByteBuf createBuffer(RegistryAccess provider, byte[] buf) {
        ByteBuf newbuf = Unpooled.buffer();
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(newbuf, provider, ConnectionType.OTHER);
        buffer.writeBytes(buf);
        return buffer;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static PacketContainerDataToClient create(ResourceLocation id, RegistryFriendlyByteBuf buffer) {
        return new PacketContainerDataToClient(id, buffer);
    }

    public static PacketContainerDataToClient create(RegistryFriendlyByteBuf buf) {
        ResourceLocation containerId = buf.readResourceLocation();
        int l = buf.readInt();

        ByteBuf newbuf = Unpooled.buffer(l);
        byte[] bytes = new byte[l];
        buf.readBytes(bytes);
        newbuf.writeBytes(bytes);
        RegistryFriendlyByteBuf  buffer = new RegistryFriendlyByteBuf(newbuf, buf.registryAccess(), ConnectionType.OTHER);
        return new PacketContainerDataToClient(containerId, buffer);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            AbstractContainerMenu container = SafeClientTools.getClientPlayer().containerMenu;
            if (container instanceof GenericContainer gc) {
                IContainerDataListener listener = gc.getListener(containerId);
                if (listener != null) {
                    listener.readBuf(buffer);
                }
            }
        });
    }

}
