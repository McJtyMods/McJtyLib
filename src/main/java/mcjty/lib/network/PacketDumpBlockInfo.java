package mcjty.lib.network;

import mcjty.lib.debugtools.DumpBlockNBT;
import mcjty.lib.varia.DimensionId;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.WorldTools;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.ServerOpList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.Level;

import java.util.function.Supplier;

/**
 * Debug packet to dump block info
 */
public class PacketDumpBlockInfo {

    private DimensionId dimid;
    private BlockPos pos;
    private boolean verbose;

    public void toBytes(FriendlyByteBuf buf) {
        dimid.toBytes(buf);
        buf.writeBlockPos(pos);
        buf.writeBoolean(verbose);
    }

    public PacketDumpBlockInfo(FriendlyByteBuf buf) {
        dimid = DimensionId.fromPacket(buf);
        pos = buf.readBlockPos();
        verbose = buf.readBoolean();
    }

    public PacketDumpBlockInfo(Level world, BlockPos pos, boolean verbose) {
        this.dimid = DimensionId.fromWorld(world);
        this.pos = pos;
        this.verbose = verbose;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            MinecraftServer server = player.getCommandSenderWorld().getServer();
            ServerOpList oppedPlayers = server.getPlayerList().getOps();
            ServerOpListEntry entry = oppedPlayers.get(player.getGameProfile());
            int perm = entry == null ? server.getOperatorUserPermissionLevel() : entry.getLevel();
            if (perm >= 1) {
                Level world = WorldTools.getWorld(player.level, dimid);
                String output = DumpBlockNBT.dumpBlockNBT(world, pos, verbose);
                Logging.getLogger().log(Level.INFO, "### Server side ###");
                Logging.getLogger().log(Level.INFO, output);
            }
        });
        ctx.setPacketHandled(true);
    }
}
