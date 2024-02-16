package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.McJtyLib;
import mcjty.lib.debugtools.DumpItemNBT;
import mcjty.lib.varia.Logging;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.ServerOpList;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.Level;

/**
 * Debug packet to dump item info
 */
public record PacketDumpItemInfo(Boolean verbose) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(McJtyLib.MODID, "dumpiteminfo");

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(verbose);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static PacketDumpItemInfo create(ByteBuf buf) {
        return new PacketDumpItemInfo(buf.readBoolean());
    }

    public static PacketDumpItemInfo create(boolean verbose) {
        return new PacketDumpItemInfo(verbose);
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ctx.player().ifPresent(player -> {
                MinecraftServer server = player.getCommandSenderWorld().getServer();
                ServerOpList oppedPlayers = server.getPlayerList().getOps();
                ServerOpListEntry entry = oppedPlayers.get(player.getGameProfile());
                int perm = entry == null ? server.getOperatorUserPermissionLevel() : entry.getLevel();
                if (perm >= 1) {
                    ItemStack item = player.getMainHandItem();
                    if (!item.isEmpty()) {
                        String output = DumpItemNBT.dumpItemNBT(item, verbose);
                        Logging.getLogger().log(Level.INFO, "### Server side ###");
                        Logging.getLogger().log(Level.INFO, output);
                    }
                }
            });
        });
    }
}
