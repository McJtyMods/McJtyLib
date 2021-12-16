package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mcjty.lib.McJtyLib;
import mcjty.lib.api.container.IContainerDataListener;
import mcjty.lib.container.GenericContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketContainerDataToClient {

    private ResourceLocation id;
    private FriendlyByteBuf buffer;

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(id);
        int l = buffer.array().length;
        buf.writeInt(l);
        buf.writeBytes(buffer.array());
    }

    public PacketContainerDataToClient(FriendlyByteBuf buf) {
        id = buf.readResourceLocation();
        int l = buf.readInt();

        ByteBuf newbuf = Unpooled.buffer(l);
        byte[] bytes = new byte[l];
        buf.readBytes(bytes);
        newbuf.writeBytes(bytes);
        buffer = new FriendlyByteBuf(newbuf);
    }

    public PacketContainerDataToClient(ResourceLocation id, FriendlyByteBuf buffer) {
        this.id = id;
        this.buffer = buffer;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            AbstractContainerMenu container = McJtyLib.proxy.getClientPlayer().containerMenu;
            if (container instanceof GenericContainer) {
                GenericContainer gc = (GenericContainer) container;
                IContainerDataListener listener = gc.getListener(id);
                if (listener != null) {
                    listener.readBuf(buffer);
                }
            }
        });
        ctx.setPacketHandled(true);
    }


}
