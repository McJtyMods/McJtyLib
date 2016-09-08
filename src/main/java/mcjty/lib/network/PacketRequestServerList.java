package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

/**
 * Make a subclass of this class to implement a command that can be sent from the client (typically in a GUI)
 * and will perform some command on the server-side tile entity. The result of that command (a list of some
 * type of object) will be sent back to the client through the 'PacketListToClient' class. So typically
 * you would also make a subclass of PacketListToClient.
 *
 * @param <T> is the type of the items in the list that is requested from the server
 */
public class PacketRequestServerList<T> extends AbstractServerCommand {
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

    public PacketRequestServerList() {
    }

    public PacketRequestServerList(String modid, BlockPos pos, String command, Argument... arguments) {
        super(pos, command, arguments);
        this.modid = modid;
    }
}
