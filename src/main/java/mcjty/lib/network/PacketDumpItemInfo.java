package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.McJtyLib;
import mcjty.lib.debugtools.DumpItemNBT;
import mcjty.lib.varia.Logging;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.logging.log4j.Level;

/**
 * Debug packet to dump item info
 */
public class PacketDumpItemInfo implements IMessage {

    private ItemStack item;
    private boolean verbose;

    @Override
    public void fromBytes(ByteBuf buf) {
        item = NetworkTools.readItemStack(buf);
        verbose = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writeItemStack(buf, item);
        buf.writeBoolean(verbose);
    }

    public PacketDumpItemInfo() {
    }

    public PacketDumpItemInfo(ItemStack item, boolean verbose) {
        this.item = item;
        this.verbose = verbose;
    }

    public static class Handler implements IMessageHandler<PacketDumpItemInfo, IMessage> {
        @Override
        public IMessage onMessage(PacketDumpItemInfo message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketDumpItemInfo message, MessageContext ctx) {
            String output = DumpItemNBT.dumpItemNBT(message.item, message.verbose);
            Logging.getLogger().log(Level.INFO, "### Server side ###");
            Logging.getLogger().log(Level.INFO, output);
        }
    }
}
