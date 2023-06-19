package mcjty.lib.network;

import mcjty.lib.debugtools.DumpBlockNBT;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.LevelTools;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.ServerOpList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Debug packet to dump block info
 */
public class PacketDumpBlockInfo {

    private final ResourceKey<Level> dimid;
    private final BlockPos pos;
    private final boolean verbose;

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(dimid.location());
        buf.writeBlockPos(pos);
        buf.writeBoolean(verbose);
    }

    public PacketDumpBlockInfo(FriendlyByteBuf buf) {
        dimid = LevelTools.getId(buf.readResourceLocation());
        pos = buf.readBlockPos();
        verbose = buf.readBoolean();
    }

    public PacketDumpBlockInfo(Level world, BlockPos pos, boolean verbose) {
        this.dimid = world.dimension();
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
                Level world = LevelTools.getLevel(player.level(), dimid);
                String output = DumpBlockNBT.dumpBlockNBT(world, pos, verbose);
                Logging.getLogger().log(org.apache.logging.log4j.Level.INFO, "### Server side ###");
                Logging.getLogger().log(org.apache.logging.log4j.Level.INFO, output);
            }
        });
        ctx.setPacketHandled(true);
    }
}
