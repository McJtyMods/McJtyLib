package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.varia.Logging;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

/**
 * This is a packet that can be used to send a command from the client side (typically the GUI) to
 * a tile entity on the server side that implements CommandHandler. This will call 'executeWithResultInteger()' on
 * that command handler. A PacketIntegerFromServer will be sent back from the client.
 */
public class PacketRequestIntegerFromServer extends AbstractServerCommand {
    private String clientCommand;
    private String modid;

    public PacketRequestIntegerFromServer() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);

        clientCommand = NetworkTools.readString(buf);
        modid = NetworkTools.readString(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);

        NetworkTools.writeString(buf, clientCommand);
        NetworkTools.writeString(buf, modid);
    }

    public PacketRequestIntegerFromServer(String modid, BlockPos pos, String command, String clientCommand, Argument... arguments) {
        super(pos, command, arguments);
        this.clientCommand = clientCommand;
        this.modid = modid;
    }

    public static class Handler implements IMessageHandler<PacketRequestIntegerFromServer, IMessage> {
        @Override
        public IMessage onMessage(PacketRequestIntegerFromServer message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketRequestIntegerFromServer message, MessageContext ctx) {
            TileEntity te = ctx.getServerHandler().player.getEntityWorld().getTileEntity(message.pos);
            if(!(te instanceof CommandHandler)) {
                Logging.log("createStartScanPacket: TileEntity is not a CommandHandler!");
                return;
            }
            CommandHandler commandHandler = (CommandHandler) te;
            Integer result = commandHandler.executeWithResultInteger(message.command, message.args);
            if (result == null) {
                Logging.log("Command " + message.command + " was not handled!");
                return;
            }

            sendReplyToClient(message, result, ctx.getServerHandler().player);
       }

        private void sendReplyToClient(PacketRequestIntegerFromServer message, Integer result, EntityPlayerMP player) {
            SimpleNetworkWrapper wrapper = PacketHandler.modNetworking.get(message.modid);
            PacketIntegerFromServer msg = new PacketIntegerFromServer(message.pos, message.clientCommand, result);
            wrapper.sendTo(msg, player);
        }

    }
}
