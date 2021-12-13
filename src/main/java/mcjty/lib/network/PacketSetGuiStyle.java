package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Change the GUI style.
 */
public class PacketSetGuiStyle {

    // Package visible for unit tests
    private String style;

    public PacketSetGuiStyle(FriendlyByteBuf buf) {
        style = buf.readUtf(32767);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(style);
    }

    public PacketSetGuiStyle(String style) {
        this.style = style;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> handle(this, ctx.get()));
        ctx.get().setPacketHandled(true);
    }

    private static void handle(PacketSetGuiStyle message, NetworkEvent.Context ctx) {
        Player playerEntity = ctx.getSender();
        McJtyLib.getPreferencesProperties(playerEntity).ifPresent(p -> p.setStyle(message.style));
    }
}
