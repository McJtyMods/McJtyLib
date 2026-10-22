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
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import mcjty.lib.network.PayloadContext;

public record PacketContainerDataToClient(Identifier containerId, RegistryFriendlyByteBuf buffer) implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(McJtyLib.MODID, "containerdata");
    public static final CustomPacketPayload.Type<PacketContainerDataToClient> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketContainerDataToClient> CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeIdentifier(packet.containerId);
                byte[] array = new byte[packet.buffer().readableBytes()];
                packet.buffer().getBytes(packet.buffer().readerIndex(), array);
                buf.writeInt(array.length);
                buf.writeBytes(array);
            },
            buf -> {
                Identifier containerId = buf.readIdentifier();
                int l = buf.readInt();
                if (l < 0 || l > 1048576 || l > buf.readableBytes()) throw new IllegalArgumentException("Invalid payload length: " + l);
                ByteBuf newbuf = Unpooled.buffer(l);
                byte[] bytes = new byte[l];
                buf.readBytes(bytes);
                newbuf.writeBytes(bytes);
                RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(newbuf, buf.registryAccess());
                return new PacketContainerDataToClient(containerId, buffer);
            }
    );

    private static RegistryFriendlyByteBuf createBuffer(RegistryAccess provider, byte[] buf) {
        ByteBuf newbuf = Unpooled.buffer();
        RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(newbuf, provider);
        buffer.writeBytes(buf);
        return buffer;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static PacketContainerDataToClient create(Identifier id, RegistryFriendlyByteBuf buffer) {
        return new PacketContainerDataToClient(id, buffer);
    }

    public static PacketContainerDataToClient create(RegistryFriendlyByteBuf buf) {
        Identifier containerId = buf.readIdentifier();
        int l = buf.readInt();
                if (l < 0 || l > 1048576 || l > buf.readableBytes()) throw new IllegalArgumentException("Invalid payload length: " + l);

        ByteBuf newbuf = Unpooled.buffer(l);
        byte[] bytes = new byte[l];
        buf.readBytes(bytes);
        newbuf.writeBytes(bytes);
        RegistryFriendlyByteBuf  buffer = new RegistryFriendlyByteBuf(newbuf, buf.registryAccess());
        return new PacketContainerDataToClient(containerId, buffer);
    }

    public void handle(PayloadContext ctx) {
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
