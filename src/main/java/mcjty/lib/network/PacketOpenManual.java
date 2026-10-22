package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.compat.patchouli.PatchouliCompatibility;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import mcjty.lib.network.PayloadContext;

/**
 * Open the manual
 */
public record PacketOpenManual(Identifier manual, Identifier entry, Integer page) implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(McJtyLib.MODID, "openmanual");
    public static final CustomPacketPayload.Type<PacketOpenManual> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PacketOpenManual> CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, PacketOpenManual::manual,
            Identifier.STREAM_CODEC, PacketOpenManual::entry,
            ByteBufCodecs.INT, PacketOpenManual::page,
            PacketOpenManual::new
    );

    public static PacketOpenManual create(Identifier manual, Identifier entry, int page) {
        return new PacketOpenManual(manual, entry, page);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(PayloadContext ctx) {
        ctx.enqueueWork(() -> handle(this, ctx));
    }

    private static void handle(PacketOpenManual message, PayloadContext ctx) {
        Player player = ctx.player();
        PatchouliCompatibility.openBookEntry((ServerPlayer) player, message.manual, message.entry, message.page);
    }
}
