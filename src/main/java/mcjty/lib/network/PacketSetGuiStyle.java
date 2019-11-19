package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Change the GUI style.
 */
public class PacketSetGuiStyle {

    // Package visible for unit tests
    String style;

    public PacketSetGuiStyle(PacketBuffer buf) {
        style = buf.readString(32767);
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeString(style);
    }

    public PacketSetGuiStyle(String style) {
        this.style = style;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> handle(this, ctx.get()));
        ctx.get().setPacketHandled(true);
    }

    private static void handle(PacketSetGuiStyle message, NetworkEvent.Context ctx) {
        PlayerEntity playerEntity = ctx.getSender();
        McJtyLib.getPreferencesProperties(playerEntity).ifPresent(p -> p.setStyle(message.style));
    }
}
