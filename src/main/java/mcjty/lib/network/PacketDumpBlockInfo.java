package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.debugtools.DumpBlockNBT;
import mcjty.lib.varia.Logging;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.OpEntry;
import net.minecraft.server.management.OpList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.Level;

import java.util.function.Supplier;

/**
 * Debug packet to dump block info
 */
public class PacketDumpBlockInfo {

    private int dimid;
    private BlockPos pos;
    private boolean verbose;

    public void toBytes(ByteBuf buf) {
        buf.writeInt(dimid);
        NetworkTools.writePos(buf, pos);
        buf.writeBoolean(verbose);
    }

    public PacketDumpBlockInfo(ByteBuf buf) {
        dimid = buf.readInt();
        pos = NetworkTools.readPos(buf);
        verbose = buf.readBoolean();
    }

    public PacketDumpBlockInfo(World world, BlockPos pos, boolean verbose) {
        this.dimid = world.getDimension().getType().getId();
        this.pos = pos;
        this.verbose = verbose;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayerEntity player = ctx.getSender();
            MinecraftServer server = player.getEntityWorld().getServer();
            OpList oppedPlayers = server.getPlayerList().getOppedPlayers();
            OpEntry entry = oppedPlayers.getEntry(player.getGameProfile());
            int perm = entry == null ? server.getOpPermissionLevel() : entry.getPermissionLevel();
            if (perm >= 1) {
                World world = DimensionManager.getWorld(server, DimensionType.getById(dimid), false, false);    // @todo check 1.14
                String output = DumpBlockNBT.dumpBlockNBT(world, pos, verbose);
                Logging.getLogger().log(Level.INFO, "### Server side ###");
                Logging.getLogger().log(Level.INFO, output);
            }
        });
        ctx.setPacketHandled(true);
    }
}
