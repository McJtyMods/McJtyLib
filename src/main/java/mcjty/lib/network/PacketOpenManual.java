package mcjty.lib.network;

import mcjty.lib.compat.patchouli.PatchouliCompatibility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Open the manual
 */
public class PacketOpenManual {

    private ResourceLocation manual;
    private ResourceLocation entry;
    private int page;

    public PacketOpenManual(PacketBuffer buf) {
        manual = buf.readResourceLocation();
        entry = buf.readResourceLocation();
        page = buf.readInt();
    }

    public void toBytes(PacketBuffer buf) {
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
        PlayerEntity playerEntity = ctx.getSender();
        PatchouliCompatibility.openBookEntry((ServerPlayerEntity) playerEntity, message.manual, message.entry, message.page);
    }
}
