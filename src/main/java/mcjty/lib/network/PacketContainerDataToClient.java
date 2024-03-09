package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mcjty.lib.McJtyLib;
import mcjty.lib.api.container.IContainerDataListener;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.varia.SafeClientTools;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record PacketContainerDataToClient(ResourceLocation containerId, FriendlyByteBuf buffer) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(McJtyLib.MODID, "containerdata");

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(containerId);
        int l = buffer.array().length;
        buf.writeInt(l);
        buf.writeBytes(buffer.array());
    }

    @Override
    public ResourceLocation id() {
        return ID;
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

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
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
