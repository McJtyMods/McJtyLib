package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.blockcommands.ICommand;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.LevelTools;
import mcjty.lib.varia.Logging;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * This is a packet that can be used to send a command from the client side (typically the GUI) to
 * a tile entity on the server that has a ResultCommand annotated with @ServerCommand
 */
public record PacketRequestDataFromServer(ResourceKey<Level> level, BlockPos pos, String command, TypedMap params, boolean dummy) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(McJtyLib.MODID, "requestdatafromserver");
    public static final CustomPacketPayload.Type<PacketRequestDataFromServer> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketRequestDataFromServer> CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION), PacketRequestDataFromServer::level,
            BlockPos.STREAM_CODEC, PacketRequestDataFromServer::pos,
            ByteBufCodecs.STRING_UTF8, PacketRequestDataFromServer::command,
            TypedMap.STREAM_CODEC, PacketRequestDataFromServer::params,
            ByteBufCodecs.BOOL, PacketRequestDataFromServer::dummy,
            PacketRequestDataFromServer::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static PacketRequestDataFromServer create(ResourceKey<Level> type, BlockPos pos, String command, TypedMap params, boolean dummy) {
        return new PacketRequestDataFromServer(type, pos, command, params, dummy);
    }

    public static PacketRequestDataFromServer create(ResourceKey<Level> type, BlockPos pos, ICommand command, TypedMap params, boolean dummy) {
        return new PacketRequestDataFromServer(type, pos, command.name(), params, dummy);
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();

            Level world = LevelTools.getLevel(player.getCommandSenderWorld(), level);
            if (world.hasChunkAt(pos)) {
                if (world.getBlockEntity(pos) instanceof GenericTileEntity generic) {
                    TypedMap result = generic.executeServerCommandWR(command, player, params);
                    if (result != null) {
                        PacketDataFromServer msg = new PacketDataFromServer(dummy ? null : pos, command, result);
                        Networking.sendToPlayer(msg, player);
                        return;
                    }
                }

                Logging.log("Command " + command + " was not handled!");
            }
        });
    }
}
