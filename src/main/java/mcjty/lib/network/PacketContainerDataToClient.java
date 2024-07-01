package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mcjty.lib.McJtyLib;
import mcjty.lib.api.container.IContainerDataListener;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.varia.SafeClientTools;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketContainerDataToClient(ResourceLocation containerId, FriendlyByteBuf buffer) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(McJtyLib.MODID, "containerdata");
    public static final CustomPacketPayload.Type<PacketContainerDataToClient> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PacketContainerDataToClient> CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, PacketContainerDataToClient::containerId,
            NeoForgeStreamCodecs.UNBOUNDED_BYTE_ARRAY, i -> i.buffer().array(),
            PacketContainerDataToClient::new
    );

    private PacketContainerDataToClient(ResourceLocation containerId, byte[] buf) {
        this(containerId, createBuffer(buf));
    }

    private static FriendlyByteBuf createBuffer(byte[] buf) {
        ByteBuf newbuf = Unpooled.buffer();
        FriendlyByteBuf buffer = new FriendlyByteBuf(newbuf);
        buffer.writeBytes(buf);
        return buffer;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static PacketContainerDataToClient create(ResourceLocation id, FriendlyByteBuf buffer) {
        return new PacketContainerDataToClient(id, buffer);
    }

    public static PacketContainerDataToClient create(FriendlyByteBuf buf) {
        ResourceLocation containerId = buf.readResourceLocation();
        int l = buf.readInt();

        ByteBuf newbuf = Unpooled.buffer(l);
        byte[] bytes = new byte[l];
        buf.readBytes(bytes);
        newbuf.writeBytes(bytes);
        FriendlyByteBuf  buffer = new FriendlyByteBuf(newbuf);
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
