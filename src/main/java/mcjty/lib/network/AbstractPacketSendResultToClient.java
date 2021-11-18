package mcjty.lib.network;

import mcjty.lib.tileentity.GenericTileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Packet to send back the list to the client. This requires
 * that the class
 */
public abstract class AbstractPacketSendResultToClient<T> {

    private final BlockPos pos;
    private final List<T> list;
    private final String command;

    public AbstractPacketSendResultToClient(PacketBuffer buf) {
        pos = buf.readBlockPos();
        command = buf.readUtf(32767);
        int size = buf.readInt();
        if (size != -1) {
            list = new ArrayList<>(size);
            for (int i = 0 ; i < size ; i++) {
                list.add(readElement(buf));
            }
        } else {
            list = null;
        }
    }

    abstract protected T readElement(PacketBuffer buf);

    public AbstractPacketSendResultToClient(BlockPos pos, String command, List<T> list) {
        this.pos = pos;
        this.command = command;
        this.list = new ArrayList<>(list);
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(command);
        if (list == null) {
            buf.writeInt(-1);
        } else {
            buf.writeInt(list.size());
            for (T item : list) {
                writeElement(buf, item);
            }
        }
    }

    abstract protected void writeElement(PacketBuffer buf, T element);

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            GenericTileEntity.executeClientCommandHelper(pos, command, list);
        });
        ctx.setPacketHandled(true);
    }

}
