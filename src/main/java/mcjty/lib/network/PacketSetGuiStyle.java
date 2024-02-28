package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Change the GUI style.
 */
public record PacketSetGuiStyle(String style) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(McJtyLib.MODID, "setguistyle");

    public static PacketSetGuiStyle create(FriendlyByteBuf buf) {
        return new PacketSetGuiStyle(buf.readUtf(32767));
    }

    public static PacketSetGuiStyle create(String style) {
        return new PacketSetGuiStyle(style);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(style);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> handle(this, ctx));
    }

    private static void handle(PacketSetGuiStyle message, PlayPayloadContext ctx) {
        ctx.player().ifPresent(player -> {
            McJtyLib.getPreferencesProperties(player).ifPresent(p -> p.setStyle(message.style));
        });
    }
}
