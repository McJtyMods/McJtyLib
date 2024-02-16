package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.compat.patchouli.PatchouliCompatibility;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Open the manual
 */
public record PacketOpenManual(ResourceLocation manual, ResourceLocation entry, Integer page) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(McJtyLib.MODID, "openmanual");

    public static PacketOpenManual create(FriendlyByteBuf buf) {
        ResourceLocation manual = buf.readResourceLocation();
        ResourceLocation entry = buf.readResourceLocation();
        Integer page = buf.readInt();
        return new PacketOpenManual(manual, entry, page);
    }

    public static Object create(ResourceLocation manual, ResourceLocation entry, int page) {
        return new PacketOpenManual(manual, entry, page);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(manual);
        buf.writeResourceLocation(entry);
        buf.writeInt(page);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> handle(this, ctx));
    }

    private static void handle(PacketOpenManual message, PlayPayloadContext ctx) {
        ctx.player().ifPresent(player -> {
            PatchouliCompatibility.openBookEntry((ServerPlayer) player, message.manual, message.entry, message.page);
        });
    }
}
