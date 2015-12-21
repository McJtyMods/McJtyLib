package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.varia.Logging;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;

/**
 * This is typically used in combination with PacketRequestListFromServer although you can also use it standalone.
 * You use this by making a subclass of this class. This implements a message that is sent from the server back to the client.
 *
 * @param <S> is the type of the subclass of this class. i.e. the class you're implementing
 * @param <T> is the type of the items in the list that is requested from the server
 */
public abstract class PacketListFromServer<S extends PacketListFromServer, T extends ByteBufConverter> implements IMessage {

    public BlockPos pos;
    public List<T> list;
    public String command;

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = NetworkTools.readPos(buf);
        command = NetworkTools.readString(buf);

        int size = buf.readInt();
        if (size != -1) {
            list = new ArrayList<T>(size);
            for (int i = 0 ; i < size ; i++) {
                T item = createItem(buf);
                list.add(item);
            }
        } else {
            list = null;
        }
    }

    protected abstract T createItem(ByteBuf buf);

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writePos(buf, pos);

        NetworkTools.writeString(buf, command);

        if (list == null) {
            buf.writeInt(-1);
        } else {
            buf.writeInt(list.size());
            for (T item : list) {
                item.toBytes(buf);
            }
        }
    }

    public PacketListFromServer() {
    }

    public PacketListFromServer(BlockPos pos, String command, List<T> list){
        this.pos = pos;
        this.command = command;
        this.list = new ArrayList<T>();
        this.list.addAll(list);
    }

    public class Handler implements IMessageHandler<S, IMessage> {
        @Override
        public IMessage onMessage(S message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(S message, MessageContext ctx) {
            TileEntity te = Minecraft.getMinecraft().theWorld.getTileEntity(message.pos);
            if(!(te instanceof ClientCommandHandler)) {
                Logging.log("createInventoryReadyPacket: TileEntity is not a ClientCommandHandler!");
                return;
            }
            ClientCommandHandler clientCommandHandler = (ClientCommandHandler) te;
            if (!clientCommandHandler.execute(message.command, message.list)) {
                Logging.log("Command " + message.command + " was not handled!");
            }
        }
    }
}
