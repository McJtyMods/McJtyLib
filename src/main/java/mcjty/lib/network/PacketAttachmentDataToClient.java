package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mcjty.lib.McJtyLib;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.varia.SafeClientTools;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketAttachmentDataToClient(ResourceLocation attachmentTypeId, RegistryFriendlyByteBuf buffer) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(McJtyLib.MODID, "attachmentdata");
    public static final Type<PacketAttachmentDataToClient> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketAttachmentDataToClient> CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeResourceLocation(packet.attachmentTypeId);
                byte[] array = packet.buffer().array();
                buf.writeInt(array.length);
                buf.writeBytes(array);
            },
            buf -> {
                ResourceLocation containerId = buf.readResourceLocation();
                int l = buf.readInt();
                ByteBuf newbuf = Unpooled.buffer(l);
                byte[] bytes = new byte[l];
                buf.readBytes(bytes);
                newbuf.writeBytes(bytes);
                RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(newbuf, buf.registryAccess(), ConnectionType.OTHER);
                return new PacketAttachmentDataToClient(containerId, buffer);
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static PacketAttachmentDataToClient create(ResourceLocation id, RegistryFriendlyByteBuf buffer) {
        return new PacketAttachmentDataToClient(id, buffer);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            AbstractContainerMenu container = SafeClientTools.getClientPlayer().containerMenu;
            if (container instanceof GenericContainer gc) {
                gc.receiveData(attachmentTypeId, buffer);
            }
        });
    }

}
