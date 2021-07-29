package mcjty.lib.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * This is sent from the server to the client after the login has occured so that packets that implement
 * IClientServerDelayed can be sent
 */
public class PacketFinalizeLogin {

    public void toBytes(FriendlyByteBuf buf) {
    }

    public PacketFinalizeLogin(FriendlyByteBuf buf) {
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