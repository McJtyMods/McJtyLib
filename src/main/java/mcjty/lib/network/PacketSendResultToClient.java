package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.blockcommands.CommandInfo;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.SafeClientTools;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Packet to send back the list to the client. This requires
 * that the command is registered to McJtyLib.registerListCommandInfo
 */
public class PacketSendResultToClient {

    private final BlockPos pos;
    private final List list;
    private final String command;

    public PacketSendResultToClient(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
        command = buf.readUtf(32767);
        CommandInfo<?> info = McJtyLib.getCommandInfo(command);
        if (info == null) {
            throw new IllegalStateException("Command '" + command + "' is not registered!");
        }
        Function<FriendlyByteBuf, ?> deserializer = info.getDeserializer();
        int size = buf.readInt();
        if (size != -1) {
            list = new ArrayList<>(size);
            for (int i = 0 ; i < size ; i++) {
                list.add(deserializer.apply(buf));
            }
        } else {
            list = null;
        }
    }

    public PacketSendResultToClient(BlockPos pos, String command, List list) {
        this.pos = pos;
        this.command = command;
        this.list = new ArrayList<>(list);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(command);
        CommandInfo<?> info = McJtyLib.getCommandInfo(command);
        if (info == null) {
            throw new IllegalStateException("Command '" + command + "' is not registered!");
        }
        BiConsumer<FriendlyByteBuf, Object> serializer = (BiConsumer<FriendlyByteBuf, Object>) info.getSerializer();
        if (serializer == null) {
            throw new IllegalStateException("Command '" + command + "' is not registered!");
        }
        if (list == null) {
            buf.writeInt(-1);
        } else {
            buf.writeInt(list.size());
            for (Object item : list) {
                serializer.accept(buf, item);
            }
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            BlockEntity te = SafeClientTools.getClientWorld().getBlockEntity(pos);
            if (te instanceof GenericTileEntity) {
                ((GenericTileEntity) te).handleListFromServer(command, SafeClientTools.getClientPlayer(), TypedMap.EMPTY, list);
            } else {
                Logging.logError("Can't handle command '" + command + "'!");
            }
        });
        ctx.setPacketHandled(true);
    }

}
