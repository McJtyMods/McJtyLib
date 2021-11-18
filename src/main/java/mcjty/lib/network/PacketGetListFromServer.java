package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.blockcommands.CommandInfo;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.LevelTools;
import mcjty.lib.varia.Logging;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

/**
 * This is executed clientside
 * Packet to get a list from the server using a @ServerCommand/ListCommand
 */
public class PacketGetListFromServer {

    protected final RegistryKey<World> dimension;
    protected final BlockPos pos;
    protected final String command;
    protected final TypedMap params;

    public PacketGetListFromServer(PacketBuffer buf) {
        dimension = LevelTools.getId(buf.readResourceLocation());
        pos = buf.readBlockPos();
        command = buf.readUtf(32767);
        params = TypedMapTools.readArguments(buf);
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeResourceLocation(dimension.location());
        buf.writeBlockPos(pos);
        buf.writeUtf(command);
        TypedMapTools.writeArguments(buf, params);
    }

    public PacketGetListFromServer(RegistryKey<World> dimension, BlockPos pos, String cmd, @Nonnull TypedMap params) {
        this.dimension = dimension;
        this.pos = pos;
        this.command = cmd;
        this.params = params;
    }

    public PacketGetListFromServer(BlockPos pos, String cmd, @Nonnull TypedMap params) {
        this.dimension = McJtyLib.proxy.getWorld().dimension();
        this.pos = pos;
        this.command = cmd;
        this.params = params;
    }

    public PacketGetListFromServer(BlockPos pos, String cmd) {
        this.dimension = McJtyLib.proxy.getWorld().dimension();
        this.pos = pos;
        this.command = cmd;
        this.params = TypedMap.EMPTY;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayerEntity player = ctx.getSender();
            ServerWorld world = LevelTools.getLevel(ctx.getSender().getCommandSenderWorld(), dimension);
            if (world.hasChunkAt(pos)) {
                TileEntity te = world.getBlockEntity(pos);
                if (te instanceof GenericTileEntity) {
                    CommandInfo<?> info = McJtyLib.getCommandInfo(command);
                    Class type = info.getType();
                    if (type == null) {
                        throw new IllegalStateException("Command '" + command + "' is not registered!");
                    }
                    List list = ((GenericTileEntity) te).executeServerCommandList(command, player, params, type);
                    McJtyLib.networkHandler.sendTo(new PacketSendResultToClient(pos, command, list), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                } else {
                    Logging.logError("Command '" + command + "' not handled!");
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
