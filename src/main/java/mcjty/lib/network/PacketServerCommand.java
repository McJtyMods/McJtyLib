package mcjty.lib.network;

import mcjty.lib.varia.Logging;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This is a packet that can be used to send a command from the client side (typically the GUI) to
 * a tile entity on the server side that implements CommandHandler. This will call 'execute()' on
 * that command handler.
 */
public class PacketServerCommand extends AbstractServerCommand {

    public PacketServerCommand() {
    }

    public PacketServerCommand(BlockPos pos, String command, Argument... arguments) {
        super(pos, command, arguments);
    }

    public static class Handler implements IMessageHandler<PacketServerCommand, IMessage> {
        @Override
        public IMessage onMessage(PacketServerCommand message, MessageContext ctx) {
            MinecraftServer.getServer().addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        public void handle(PacketServerCommand message, MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;
            TileEntity te = playerEntity.worldObj.getTileEntity(message.pos);
            if(!(te instanceof CommandHandler)) {
                Logging.log("createStartScanPacket: TileEntity is not a CommandHandler!");
                return;
            }
            CommandHandler commandHandler = (CommandHandler) te;
            if (!commandHandler.execute(playerEntity, message.command, message.args)) {
                Logging.log("Command " + message.command + " was not handled!");
            }
        }
    }
}
