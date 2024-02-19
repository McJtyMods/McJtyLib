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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * This is executed clientside
 * Packet to get a list from the server using a @ServerCommand/ListCommand
 */
public record PacketGetListFromServer(ResourceKey<Level> dimension, BlockPos pos, String command, TypedMap params) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(McJtyLib.MODID, "getlistfromserver");

    public static PacketGetListFromServer create(FriendlyByteBuf buf) {
        ResourceKey<Level> dimension = LevelTools.getId(buf.readResourceLocation());
        BlockPos pos = buf.readBlockPos();
        String command = buf.readUtf(32767);
        TypedMap params = TypedMapTools.readArguments(buf);
        return new PacketGetListFromServer(dimension, pos, command, params);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(dimension.location());
        buf.writeBlockPos(pos);
        buf.writeUtf(command);
        TypedMapTools.writeArguments(buf, params);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static PacketGetListFromServer create(BlockPos pos, String cmd, @Nonnull TypedMap params) {
        ResourceKey<Level> dimension = SafeClientTools.getWorld().dimension();
        return new PacketGetListFromServer(dimension, pos, cmd, params);
    }

    public static PacketGetListFromServer create(BlockPos pos, String cmd) {
        ResourceKey<Level> dimension = SafeClientTools.getWorld().dimension();
        return new PacketGetListFromServer(dimension, pos, cmd, TypedMap.EMPTY);
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ctx.player().ifPresent(player -> {
                ServerLevel world = LevelTools.getLevel(player.getCommandSenderWorld(), dimension);
                if (world.hasChunkAt(pos)) {
                    BlockEntity te = world.getBlockEntity(pos);
                    if (te instanceof GenericTileEntity generic) {
                        CommandInfo<?> info = McJtyLib.getCommandInfo(command);
                        if (info == null) {
                            throw new IllegalStateException("Command '" + command + "' is not registered!");
                        }
                        Class type = info.type();
                        List list = generic.executeServerCommandList(command, player, params, type);
                        McJtyLib.sendToPlayer(new PacketSendResultToClient(pos, command, list), player);
                    } else {
                        Logging.logError("Command '" + command + "' not handled!");
                    }
                }
            });
        });
    }
}
