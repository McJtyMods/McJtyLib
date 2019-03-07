package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.typed.TypedMap;
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
public class PacketRequestDataFromServer implements IMessage {
    protected BlockPos pos;
    protected String command;
    protected TypedMap params;
    private String modid;

    public PacketRequestDataFromServer() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = NetworkTools.readPos(buf);
        command = NetworkTools.readString(buf);
        params = TypedMapTools.readArguments(buf);
        modid = NetworkTools.readString(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writePos(buf, pos);
        NetworkTools.writeString(buf, command);
        TypedMapTools.writeArguments(buf, params);
        NetworkTools.writeString(buf, modid);
    }

    public PacketRequestDataFromServer(String modid, BlockPos pos, String command, TypedMap params) {
        this.pos = pos;
        this.command = command;
        this.params = params;
        this.modid = modid;
    }

    public static class Handler implements IMessageHandler<PacketRequestDataFromServer, IMessage> {
        @Override
        public IMessage onMessage(PacketRequestDataFromServer message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketRequestDataFromServer message, MessageContext ctx) {
            TileEntity te = ctx.getServerHandler().player.getEntityWorld().getTileEntity(message.pos);
            if(!(te instanceof ICommandHandler)) {
                Logging.log("createStartScanPacket: TileEntity is not a CommandHandler!");
                return;
            }
            ICommandHandler commandHandler = (ICommandHandler) te;
            TypedMap result = commandHandler.executeWithResult(message.command, message.params);
            if (result == null) {
                Logging.log("Command " + message.command + " was not handled!");
                return;
            }

            sendReplyToClient(message, result, ctx.getServerHandler().player);
       }

        private void sendReplyToClient(PacketRequestDataFromServer message, TypedMap result, EntityPlayerMP player) {
            SimpleNetworkWrapper wrapper = PacketHandler.modNetworking.get(message.modid);
            PacketDataFromServer msg = new PacketDataFromServer(message.pos, message.command, result);
            wrapper.sendTo(msg, player);
        }

    }
}
