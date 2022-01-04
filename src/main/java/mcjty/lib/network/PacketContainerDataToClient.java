package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import mcjty.lib.api.container.IContainerDataListener;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.varia.SafeClientTools;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketContainerDataToClient {

    private ResourceLocation id;
    private PacketBuffer buffer;

    public void toBytes(PacketBuffer buf) {
        buf.writeResourceLocation(id);
        int l = buffer.array().length;
        buf.writeInt(l);
        buf.writeBytes(buffer.array());
    }

    public PacketContainerDataToClient(PacketBuffer buf) {
        id = buf.readResourceLocation();
        int l = buf.readInt();

        ByteBuf newbuf = Unpooled.buffer(l);
        byte[] bytes = new byte[l];
        buf.readBytes(bytes);
        newbuf.writeBytes(bytes);
        buffer = new PacketBuffer(newbuf);
    }

    public PacketContainerDataToClient(ResourceLocation id, PacketBuffer buffer) {
        this.id = id;
        this.buffer = buffer;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Container container = SafeClientTools.getClientPlayer().containerMenu;
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
