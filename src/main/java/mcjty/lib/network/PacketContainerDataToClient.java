package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.api.container.IContainerDataListener;
import mcjty.lib.container.GenericContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class PacketContainerDataToClient {

    private ResourceLocation id;

    // For writing use the consumer
    private Consumer<PacketBuffer> consumer;

    // For reading we use this buffer
    private PacketBuffer buffer;

    public void toBytes(PacketBuffer buf) {
        buf.writeResourceLocation(id);
        consumer.accept(buf);
    }

    public PacketContainerDataToClient(PacketBuffer buf) {
        id = buf.readResourceLocation();
        buffer = new PacketBuffer(buf.readBytes(buf));
    }

    public PacketContainerDataToClient(Consumer<PacketBuffer> consumer) {
        this.consumer = consumer;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Container container = McJtyLib.proxy.getClientPlayer().openContainer;
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
