package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.preferences.PreferencesProperties;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

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
            PreferencesProperties v = McJtyLib.getPreferencesProperties(player);
            if (v != null) {
                v.setStyle(message.style);
            }
        });
    }
}
