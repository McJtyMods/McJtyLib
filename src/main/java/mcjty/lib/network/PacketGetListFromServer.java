package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.blockcommands.CommandInfo;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.LevelTools;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.SafeClientTools;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

/**
 * This is executed clientside
 * Packet to get a list from the server using a @ServerCommand/ListCommand
 */
public class PacketGetListFromServer {

    protected final ResourceKey<Level> dimension;
    protected final BlockPos pos;
    protected final String command;
    protected final TypedMap params;

    public PacketGetListFromServer(FriendlyByteBuf buf) {
        dimension = LevelTools.getId(buf.readResourceLocation());
        pos = buf.readBlockPos();
        command = buf.readUtf(32767);
        params = TypedMapTools.readArguments(buf);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeResourceLocation(dimension.location());
        buf.writeBlockPos(pos);
        buf.writeUtf(command);
        TypedMapTools.writeArguments(buf, params);
    }

    public PacketGetListFromServer(ResourceKey<Level> dimension, BlockPos pos, String cmd, @Nonnull TypedMap params) {
        this.dimension = dimension;
        this.pos = pos;
        this.command = cmd;
        this.params = params;
    }

    public PacketGetListFromServer(BlockPos pos, String cmd, @Nonnull TypedMap params) {
        this.dimension = SafeClientTools.getWorld().dimension();
        this.pos = pos;
        this.command = cmd;
        this.params = params;
    }

    public PacketGetListFromServer(BlockPos pos, String cmd) {
        this.dimension = SafeClientTools.getWorld().dimension();
        this.pos = pos;
        this.command = cmd;
        this.params = TypedMap.EMPTY;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            ServerLevel world = LevelTools.getLevel(ctx.getSender().getCommandSenderWorld(), dimension);
            if (world.hasChunkAt(pos)) {
                BlockEntity te = world.getBlockEntity(pos);
                if (te instanceof GenericTileEntity generic) {
                    CommandInfo<?> info = McJtyLib.getCommandInfo(command);
                    if (info == null) {
                        throw new IllegalStateException("Command '" + command + "' is not registered!");
                    }
                    Class type = info.type();
                    List list = generic.executeServerCommandList(command, player, params, type);
                    McJtyLib.networkHandler.sendTo(new PacketSendResultToClient(pos, command, list), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                } else {
                    Logging.logError("Command '" + command + "' not handled!");
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}
