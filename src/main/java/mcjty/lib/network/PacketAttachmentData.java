package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mcjty.lib.McJtyLib;
import mcjty.lib.container.GenericContainer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import mcjty.lib.network.PayloadContext;

public record PacketAttachmentData(Identifier attachmentTypeId, RegistryFriendlyByteBuf buffer) implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(McJtyLib.MODID, "attachmentdata");
    public static final Type<PacketAttachmentData> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketAttachmentData> CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeIdentifier(packet.attachmentTypeId);
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
                return new PacketAttachmentData(containerId, buffer);
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static PacketAttachmentData create(Identifier id, RegistryFriendlyByteBuf buffer) {
        return new PacketAttachmentData(id, buffer);
    }

    public void handle(PayloadContext ctx) {
        ctx.enqueueWork(() -> {
            AbstractContainerMenu container = ctx.player().containerMenu;
            if (container instanceof GenericContainer gc) {
                gc.receiveData(attachmentTypeId, buffer);
            }
        });
    }

}
