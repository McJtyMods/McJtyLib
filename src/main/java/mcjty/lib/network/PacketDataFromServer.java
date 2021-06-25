package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nullable;
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
        if (pos != null) {
            buf.writeBoolean(true);
            buf.writeBlockPos(pos);
        } else {
            buf.writeBoolean(false);
        }
        buf.writeUtf(command);

        buf.writeBoolean(result != null);
        if (result != null) {
            TypedMapTools.writeArguments(buf, result);
        }
    }

    public PacketDataFromServer(PacketBuffer buf) {
        if (buf.readBoolean()) {
            pos = buf.readBlockPos();
        } else {
            pos = null;
        }
        command = buf.readUtf(32767);

        if (buf.readBoolean()) {
            result = TypedMapTools.readArguments(buf);
        } else {
            result = null;
        }
    }

    public PacketDataFromServer(@Nullable BlockPos pos, String command, TypedMap result) {
        this.pos = pos;
        this.command = command;
        this.result = result;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            TileEntity te;
            if (pos == null) {
                // We are working from a tablet. Find the tile entity through the open container
                GenericContainer container = getOpenContainer();
                if (container == null) {
                    Logging.log("Container is missing!");
                    return;
                }
                te = container.getTe();
            } else {
                te = McJtyLib.proxy.getClientWorld().getBlockEntity(pos);
                if (!(te instanceof IClientCommandHandler)) {
                    Logging.log("TileEntity is not a ClientCommandHandler!");
                    return;
                }
            }

            IClientCommandHandler clientCommandHandler = (IClientCommandHandler) te;
            if (!clientCommandHandler.receiveDataFromServer(command, result)) {
                Logging.log("Command " + command + " was not handled!");
            }
        });
        ctx.setPacketHandled(true);
    }

    private static GenericContainer getOpenContainer() {
        Container container = McJtyLib.proxy.getClientPlayer().containerMenu;
        if (container instanceof GenericContainer) {
            return (GenericContainer) container;
        } else {
            return null;
        }
    }
}
