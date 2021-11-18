package mcjty.lib.network;

import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

/**
 * Abstract packet to get a list from the server using a @ServerCommand/ListCommand
 */
public abstract class AbstractPacketGetListFromServer<T> {

    protected final BlockPos pos;
    protected final String command;
    protected final TypedMap params;

    public AbstractPacketGetListFromServer(PacketBuffer buf) {
        pos = buf.readBlockPos();
        command = buf.readUtf(32767);
        params = TypedMapTools.readArguments(buf);
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(command);
        TypedMapTools.writeArguments(buf, params);
    }

    public AbstractPacketGetListFromServer(BlockPos pos, String cmd, @Nonnull TypedMap params) {
        this.pos = pos;
        this.command = cmd;
        this.params = params;
    }

    abstract protected SimpleChannel getChannel();

    abstract protected Class<T> getType();

    abstract protected Object createReturnPacket(List<T> list);

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayerEntity player = ctx.getSender();
            ServerWorld world = player.getLevel();
            if (world.hasChunkAt(pos)) {
                TileEntity te = world.getBlockEntity(pos);
                if (te instanceof GenericTileEntity) {
                    List<T> list = ((GenericTileEntity) te).executeServerCommandList(command, player, params, getType());
                    getChannel().sendTo(createReturnPacket(list), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                } else {
                    Logging.logError("Command '" + command + "' not handled!");
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
