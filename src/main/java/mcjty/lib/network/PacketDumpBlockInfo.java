package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.debugtools.DumpBlockNBT;
import mcjty.lib.thirteen.Context;
import mcjty.lib.varia.Logging;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOps;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.apache.logging.log4j.Level;

import java.util.function.Supplier;

/**
 * Debug packet to dump block info
 */
public class PacketDumpBlockInfo implements IMessage {

    private int dimid;
    private BlockPos pos;
    private boolean verbose;

    @Override
    public void fromBytes(ByteBuf buf) {
        dimid = buf.readInt();
        pos = NetworkTools.readPos(buf);
        verbose = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(dimid);
        NetworkTools.writePos(buf, pos);
        buf.writeBoolean(verbose);
    }

    public PacketDumpBlockInfo() {
    }

    public PacketDumpBlockInfo(ByteBuf buf) {
        fromBytes(buf);
    }

    public PacketDumpBlockInfo(World world, BlockPos pos, boolean verbose) {
        this.dimid = world.provider.getDimension();
        this.pos = pos;
        this.verbose = verbose;
    }

    public void handle(Supplier<Context> supplier) {
        Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            EntityPlayerMP player = ctx.getSender();
            MinecraftServer server = player.getEntityWorld().getMinecraftServer();
            UserListOps oppedPlayers = server.getPlayerList().getOppedPlayers();
            UserListOpsEntry entry = oppedPlayers.getEntry(player.getGameProfile());
            int perm = entry == null ? server.getOpPermissionLevel() : entry.getPermissionLevel();
            if (perm >= 1) {
                World world = DimensionManager.getWorld(dimid);
                String output = DumpBlockNBT.dumpBlockNBT(world, pos, verbose);
                Logging.getLogger().log(Level.INFO, "### Server side ###");
                Logging.getLogger().log(Level.INFO, output);
            }
        });
        ctx.setPacketHandled(true);
    }
}
