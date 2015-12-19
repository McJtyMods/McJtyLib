package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import mcjty.lib.varia.Logging;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This is a packet that can be used to send a command from the client side (typically the GUI) to
 * a tile entity on the server side that implements CommandHandler. This will call 'executeWithResultInteger()' on
 * that command handler. A PacketIntegerFromServer will be sent back from the client.
 */
public class PacketRequestIntegerFromServer extends AbstractServerCommand {
    private String clientCommand;

    public PacketRequestIntegerFromServer() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);

        clientCommand = NetworkTools.readString(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);

        NetworkTools.writeString(buf, clientCommand);
    }

    public PacketRequestIntegerFromServer(BlockPos pos, String command, String clientCommand, Argument... arguments) {
        super(pos, command, arguments);
        this.clientCommand = clientCommand;
    }

    public static class Handler implements IMessageHandler<PacketRequestIntegerFromServer, IMessage> {
        @Override
        public IMessage onMessage(PacketRequestIntegerFromServer message, MessageContext ctx) {
            MinecraftServer.getServer().addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketRequestIntegerFromServer message, MessageContext ctx) {
            TileEntity te = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.pos);
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
            sendReplyToClient(ctx.getServerHandler().getNetworkManager(), message, result, ctx.getServerHandler().playerEntity);
       }

        private void sendReplyToClient(NetworkManager network, PacketRequestIntegerFromServer message, Integer result, EntityPlayerMP player) {
            Channel channel = network.channel();
            channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
            channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
            channel.writeAndFlush(new PacketIntegerFromServer(message.pos, message.clientCommand, result)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }

    }
}
