package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

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
public abstract class PacketRequestListFromServer<T, S extends PacketRequestListFromServer<T, S, C>, C extends PacketListFromServer<C,T>> extends AbstractServerCommand {
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
}
