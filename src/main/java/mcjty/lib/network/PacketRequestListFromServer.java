package mcjty.lib.network;

import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import mcjty.lib.varia.Logging;
import net.minecraft.tileentity.TileEntity;

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

public abstract class PacketRequestListFromServer<T extends ByteBufConverter, S extends PacketRequestListFromServer, C extends PacketListFromServer<C,T>> extends AbstractServerCommand implements IMessageHandler<S, C> {
    public PacketRequestListFromServer() {
    }

    public PacketRequestListFromServer(BlockPos pos, String command, Argument... arguments) {
        super(pos, command, arguments);
    }

    @Override
    public C onMessage(S message, MessageContext ctx) {
        TileEntity te = ctx.getServerHandler().playerEntity.worldObj.getTileEntity(message.pos);
        if(!(te instanceof CommandHandler)) {
            Logging.log("createStartScanPacket: TileEntity is not a CommandHandler!");
            return null;
        }
        CommandHandler commandHandler = (CommandHandler) te;
        List<T> list = (List<T>) commandHandler.executeWithResultList(message.command, message.args);
        if (list == null) {
            Logging.log("Command " + message.command + " was not handled!");
            return null;
        }
        return createMessageToClient(message.pos, list);
    }

    protected abstract C createMessageToClient(BlockPos pos, List<T> result);
}
