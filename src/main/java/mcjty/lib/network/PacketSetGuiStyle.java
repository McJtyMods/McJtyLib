package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.preferences.PreferencesProperties;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Change the GUI style.
 */
public record PacketSetGuiStyle(String style) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(McJtyLib.MODID, "setguistyle");
    public static final CustomPacketPayload.Type<PacketSetGuiStyle> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PacketSetGuiStyle> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, PacketSetGuiStyle::style, PacketSetGuiStyle::new
    );

    public static PacketSetGuiStyle create(String style) {
        return new PacketSetGuiStyle(style);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> handle(this, ctx));
    }

    private static void handle(PacketSetGuiStyle message, IPayloadContext ctx) {
        Player player = ctx.player();
        PreferencesProperties v = McJtyLib.getPreferencesProperties(player);
        if (v != null) {
            v.setStyle(message.style);
        }
    }
}
