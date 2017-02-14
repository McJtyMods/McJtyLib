package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.debugtools.DumpBlockNBT;
import mcjty.lib.varia.Logging;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOps;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.logging.log4j.Level;

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

    public PacketDumpBlockInfo(World world, BlockPos pos, boolean verbose) {
        this.dimid = world.provider.getDimension();
        this.pos = pos;
        this.verbose = verbose;
    }

    public static class Handler implements IMessageHandler<PacketDumpBlockInfo, IMessage> {
        @Override
        public IMessage onMessage(PacketDumpBlockInfo message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketDumpBlockInfo message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            MinecraftServer server = player.getEntityWorld().getMinecraftServer();
            UserListOps oppedPlayers = server.getPlayerList().getOppedPlayers();
            UserListOpsEntry entry = oppedPlayers.getEntry(player.getGameProfile());
            int perm = entry == null ? server.getOpPermissionLevel() : entry.getPermissionLevel();
            if (perm >= 1) {
                World world = DimensionManager.getWorld(message.dimid);
                String output = DumpBlockNBT.dumpBlockNBT(world, message.pos, message.verbose);
                Logging.getLogger().log(Level.INFO, "### Server side ###");
                Logging.getLogger().log(Level.INFO, output);
            }
        }
    }
}
