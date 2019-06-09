package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.debugtools.DumpItemNBT;
import mcjty.lib.thirteen.Context;
import mcjty.lib.varia.Logging;
import net.minecraft.entity.player.PlayerEntityMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListOps;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.apache.logging.log4j.Level;

import java.util.function.Supplier;

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

    public PacketDumpItemInfo(ByteBuf buf) {
        fromBytes(buf);
    }

    public PacketDumpItemInfo(boolean verbose) {
        this.verbose = verbose;
    }

    public void handle(Supplier<Context> supplier) {
        Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            PlayerEntityMP player = ctx.getSender();
            MinecraftServer server = player.getEntityWorld().getMinecraftServer();
            UserListOps oppedPlayers = server.getPlayerList().getOppedPlayers();
            UserListOpsEntry entry = oppedPlayers.getEntry(player.getGameProfile());
            int perm = entry == null ? server.getOpPermissionLevel() : entry.getPermissionLevel();
            if (perm >= 1) {
                ItemStack item = player.getHeldItemMainhand();
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
