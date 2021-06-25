package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.debugtools.DumpItemNBT;
import mcjty.lib.varia.Logging;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.OpEntry;
import net.minecraft.server.management.OpList;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.Level;

import java.util.function.Supplier;

/**
 * Debug packet to dump item info
 */
public class PacketDumpItemInfo {

    private boolean verbose;

    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(verbose);
    }

    public PacketDumpItemInfo(ByteBuf buf) {
        verbose = buf.readBoolean();
    }

    public PacketDumpItemInfo(boolean verbose) {
        this.verbose = verbose;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayerEntity player = ctx.getSender();
            MinecraftServer server = player.getCommandSenderWorld().getServer();
            OpList oppedPlayers = server.getPlayerList().getOps();
            OpEntry entry = oppedPlayers.get(player.getGameProfile());
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
        ctx.setPacketHandled(true);
    }
}
