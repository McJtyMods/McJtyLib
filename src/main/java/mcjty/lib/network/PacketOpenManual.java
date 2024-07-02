package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.compat.patchouli.PatchouliCompatibility;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Open the manual
 */
public record PacketOpenManual(ResourceLocation manual, ResourceLocation entry, Integer page) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(McJtyLib.MODID, "openmanual");
    public static final CustomPacketPayload.Type<PacketOpenManual> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PacketOpenManual> CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, PacketOpenManual::manual,
            ResourceLocation.STREAM_CODEC, PacketOpenManual::entry,
            ByteBufCodecs.INT, PacketOpenManual::page,
            PacketOpenManual::new
    );

    public static PacketOpenManual create(ResourceLocation manual, ResourceLocation entry, int page) {
        return new PacketOpenManual(manual, entry, page);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> handle(this, ctx));
    }

    private static void handle(PacketOpenManual message, IPayloadContext ctx) {
        Player player = ctx.player();
        PatchouliCompatibility.openBookEntry((ServerPlayer) player, message.manual, message.entry, message.page);
    }
}
