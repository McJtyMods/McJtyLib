package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * This packet is used (typically by PacketRequestDataFromServer) to send back a data to the client.
 */
@SuppressWarnings("ALL")
public class PacketDataFromServer {
    // Package visible for unittests
    BlockPos pos;
    TypedMap result;
    String command;

    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeString(command);

        buf.writeBoolean(result != null);
        if (result != null) {
            TypedMapTools.writeArguments(buf, result);
        }
    }

    public PacketDataFromServer(PacketBuffer buf) {
        pos = buf.readBlockPos();
        command = buf.readString(32767);

        if (buf.readBoolean()) {
            result = TypedMapTools.readArguments(buf);
        } else {
            result = null;
        }
    }

    public PacketDataFromServer(BlockPos pos, String command, TypedMap result) {
        this.pos = pos;
        this.command = command;
        this.result = result;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            TileEntity te = McJtyLib.proxy.getClientWorld().getTileEntity(pos);
            if(!(te instanceof IClientCommandHandler)) {
                Logging.log("createInventoryReadyPacket: TileEntity is not a ClientCommandHandler!");
                return;
            }
            IClientCommandHandler clientCommandHandler = (IClientCommandHandler) te;
            if (!clientCommandHandler.receiveDataFromServer(command, result)) {
                Logging.log("Command " + command + " was not handled!");
            }
        });
        ctx.setPacketHandled(true);
    }
}
