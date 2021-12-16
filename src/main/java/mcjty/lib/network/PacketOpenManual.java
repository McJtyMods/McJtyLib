package mcjty.lib.network;

import mcjty.lib.compat.patchouli.PatchouliCompatibility;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Open the manual
 */
public class PacketOpenManual {

    private ResourceLocation manual;
    private ResourceLocation entry;
    private int page;

    public PacketOpenManual(FriendlyByteBuf buf) {
        manual = buf.readResourceLocation();
        entry = buf.readResourceLocation();
        page = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(manual);
        buf.writeResourceLocation(entry);
        buf.writeInt(page);
    }

    public PacketOpenManual(ResourceLocation manual, ResourceLocation entry, int page) {
        this.manual = manual;
        this.entry = entry;
        this.page = page;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> handle(this, ctx.get()));
        ctx.get().setPacketHandled(true);
    }

    private static void handle(PacketOpenManual message, NetworkEvent.Context ctx) {
        Player playerEntity = ctx.getSender();
        PatchouliCompatibility.openBookEntry((ServerPlayer) playerEntity, message.manual, message.entry, message.page);
    }
}
