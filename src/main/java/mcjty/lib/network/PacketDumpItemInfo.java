package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.debugtools.DumpItemNBT;
import mcjty.lib.tools.ItemStackTools;
import mcjty.lib.varia.Logging;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOps;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.logging.log4j.Level;

/**
 * Debug packet to dump item info
 */
public class PacketDumpItemInfo implements IMessage {

    private boolean verbose;

    @Override
    public void fromBytes(ByteBuf buf) {
        verbose = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(verbose);
    }

    public PacketDumpItemInfo() {
    }

    public PacketDumpItemInfo(boolean verbose) {
        this.verbose = verbose;
    }

    public static class Handler implements IMessageHandler<PacketDumpItemInfo, IMessage> {
        @Override
        public IMessage onMessage(PacketDumpItemInfo message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketDumpItemInfo message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            MinecraftServer server = player.getEntityWorld().getMinecraftServer();
            UserListOps oppedPlayers = server.getPlayerList().getOppedPlayers();
            UserListOpsEntry entry = oppedPlayers.getEntry(player.getGameProfile());
            int perm = entry == null ? server.getOpPermissionLevel() : entry.getPermissionLevel();
            if (perm >= 1) {
                ItemStack item = player.getHeldItemMainhand();
                if (ItemStackTools.isValid(item)) {
                    String output = DumpItemNBT.dumpItemNBT(item, message.verbose);
                    Logging.getLogger().log(Level.INFO, "### Server side ###");
                    Logging.getLogger().log(Level.INFO, output);
                }
            }
        }
    }
}
