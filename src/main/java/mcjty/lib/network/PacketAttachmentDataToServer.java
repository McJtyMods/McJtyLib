package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mcjty.lib.McJtyLib;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.varia.SafeClientTools;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.network.handling.IPayloadContext;

// @todo 1.21 merge with PacketAttachmentDataToClient
public record PacketAttachmentDataToServer(ResourceLocation attachmentTypeId, RegistryFriendlyByteBuf buffer) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(McJtyLib.MODID, "attachmentdata_to_server");
    public static final Type<PacketAttachmentDataToServer> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketAttachmentDataToServer> CODEC = StreamCodec.of(
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
                return new PacketAttachmentDataToServer(containerId, buffer);
            }
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static PacketAttachmentDataToServer create(ResourceLocation id, RegistryFriendlyByteBuf buffer) {
        return new PacketAttachmentDataToServer(id, buffer);
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
