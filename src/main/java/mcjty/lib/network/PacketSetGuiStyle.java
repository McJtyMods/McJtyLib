package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.McJtyLib;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Change the GUI style.
 */
public class PacketSetGuiStyle {

    private String style;

    public PacketSetGuiStyle(ByteBuf buf) {
        style = NetworkTools.readString(buf);
    }

    public void toBytes(ByteBuf buf) {
        NetworkTools.writeString(buf, style);
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
