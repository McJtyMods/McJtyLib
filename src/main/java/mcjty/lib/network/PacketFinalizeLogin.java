package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * This is sent from the server to the client after the login has occured so that packets that implement
 * IClientServerDelayed can be sent
 */
public class PacketFinalizeLogin {

    public void toBytes(ByteBuf buf) {
    }

    public PacketFinalizeLogin(ByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        finalizeClientLogin();
        ctx.setPacketHandled(true);
    }

    private void finalizeClientLogin() {
        PacketHandler.connected = true;
    }

}