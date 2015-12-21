package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.varia.Logging;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import java.util.List;

/**
 * Make a subclass of this class to implement a command that can be sent from the client (typically in a GUI)
 * and will perform some command on the server-side tile entity. The result of that command (a list of some
 * type of object) will be sent back to the client through the 'PacketListFromServer' class. So typically
 * you would also make a subclass of PacketListFromServer.
 *
 * The items of this list should implement ByteBufConverter.
 *
 * @param <T> is the type of the items in the list that is requested from the server
 * @param <S> is the type of the subclass of this class. i.e. the class you're implementing
 * @param <C> is the type of the subclass of PacketListFromServer. i.e. the class sent back from the server.
 */
public abstract class PacketRequestListFromServer<T extends ByteBufConverter, S extends PacketRequestListFromServer, C extends PacketListFromServer<C,T>> extends AbstractServerCommand {
    public String modid;

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        modid = NetworkTools.readString(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        NetworkTools.writeString(buf, modid);
    }

    public PacketRequestListFromServer() {
    }

    public PacketRequestListFromServer(String modid, BlockPos pos, String command, Argument... arguments) {
        super(pos, command, arguments);
        this.modid = modid;
    }

    public class Handler implements IMessageHandler<S, IMessage> {
        @Override
        public IMessage onMessage(S message, MessageContext ctx) {
            MinecraftServer.getServer().addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(S message, MessageContext ctx) {
            TileEntity te = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.pos);
            if(!(te instanceof CommandHandler)) {
                Logging.log("createStartScanPacket: TileEntity is not a CommandHandler!");
                return;
            }
            CommandHandler commandHandler = (CommandHandler) te;
            List<T> list = (List<T>) commandHandler.executeWithResultList(message.command, message.args);
            if (list == null) {
                Logging.log("Command " + message.command + " was not handled!");
                return;
            }
            SimpleNetworkWrapper wrapper = PacketHandler.modNetworking.get(message.modid);
            C msg = createMessageToClient(message.pos, list);
            wrapper.sendTo(msg, ctx.getServerHandler().playerEntity);
        }

    }

    protected abstract C createMessageToClient(BlockPos pos, List<T> result);
}
