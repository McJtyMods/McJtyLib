package mcjty.lib.network;

import mcjty.lib.varia.Logging;
import mcjty.typed.Type;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Handler for PacketRequestServerList
 *
 * @param <M> is the type of the PacketRequestServerList instance
 * @param <T> is the type of the items in the list that is requested from the server
 */
public abstract class PacketRequestServerListHandler<M extends PacketRequestServerList<T>, T> implements IMessageHandler<M, IMessage> {

    private final Type<T> type;

    public PacketRequestServerListHandler(Type<T> type) {
        this.type = type;
    }

    @Override
    public IMessage onMessage(M message, MessageContext ctx) {
        FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
        return null;
    }

    private void handle(M message, MessageContext ctx) {
        TileEntity te = ctx.getServerHandler().player.getEntityWorld().getTileEntity(message.pos);
        if(!(te instanceof CommandHandler)) {
            Logging.log("TileEntity is not a CommandHandler!");
            return;
        }
        CommandHandler commandHandler = (CommandHandler) te;
        List<T> list = commandHandler.executeWithResultList(message.command, message.args, type);
        sendToClient(message.pos, list, ctx);
    }

    protected abstract void sendToClient(BlockPos pos, @Nonnull List<T> list, MessageContext ctx);
}
