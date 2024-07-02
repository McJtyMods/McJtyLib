package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.blockcommands.CommandInfo;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.LevelTools;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.SafeClientTools;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * This is executed clientside
 * Packet to get a list from the server using a @ServerCommand/ListCommand
 */
public record PacketGetListFromServer(ResourceKey<Level> dimension, BlockPos pos, String command, TypedMap params) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(McJtyLib.MODID, "getlistfromserver");
    public static final CustomPacketPayload.Type<PacketGetListFromServer> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketGetListFromServer> CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION), PacketGetListFromServer::dimension,
            BlockPos.STREAM_CODEC, PacketGetListFromServer::pos,
            ByteBufCodecs.STRING_UTF8, PacketGetListFromServer::command,
            TypedMap.STREAM_CODEC, PacketGetListFromServer::params,
            PacketGetListFromServer::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static PacketGetListFromServer create(BlockPos pos, String cmd, @Nonnull TypedMap params) {
        ResourceKey<Level> dimension = SafeClientTools.getWorld().dimension();
        return new PacketGetListFromServer(dimension, pos, cmd, params);
    }

    public static PacketGetListFromServer create(BlockPos pos, String cmd) {
        ResourceKey<Level> dimension = SafeClientTools.getWorld().dimension();
        return new PacketGetListFromServer(dimension, pos, cmd, TypedMap.EMPTY);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
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
                    Networking.sendToPlayer(new PacketSendResultToClient(pos, command, list), player);
                } else {
                    Logging.logError("Command '" + command + "' not handled!");
                }
            }
        });
    }
}
