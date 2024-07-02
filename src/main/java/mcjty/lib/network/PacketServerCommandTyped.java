package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.CodecTools;
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

import java.util.Optional;

/**
 * This is a packet that can be used to send a command from the client side (typically the GUI) to
 * a tile entity on the server side that implements CommandHandler. This will call 'execute()' on
 * that command handler.
 */
public record PacketServerCommandTyped(BlockPos pos, ResourceKey<Level> dimensionId, String command, TypedMap params) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(McJtyLib.MODID, "servercommandtyped");
    public static final CustomPacketPayload.Type<PacketServerCommandTyped> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketServerCommandTyped> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketServerCommandTyped::pos,
            CodecTools.optionalResourceKeyStreamCodec(Registries.DIMENSION), v -> Optional.ofNullable(v.dimensionId()),
            ByteBufCodecs.STRING_UTF8, PacketServerCommandTyped::command,
            TypedMap.STREAM_CODEC, PacketServerCommandTyped::params,
            PacketServerCommandTyped::new
    );

    private PacketServerCommandTyped(BlockPos pos, Optional<ResourceKey<Level>> dimensionId, String command, TypedMap params) {
        this(pos, dimensionId.orElse(null), command, params);
    }

    public static PacketServerCommandTyped create(BlockPos blockPos, ResourceKey<Level> dimension, String command, TypedMap params) {
        return new PacketServerCommandTyped(blockPos, dimension, command, params);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            Level world;
            if (dimensionId == null) {
                world = player.getCommandSenderWorld();
            } else {
                world = LevelTools.getLevel(player.level(), dimensionId);
            }
            if (world == null) {
                return;
            }
            if (world.hasChunkAt(pos)) {
                if (world.getBlockEntity(pos) instanceof GenericTileEntity generic) {
                    if (generic.executeServerCommand(command, player, params)) {
                        return;
                    }
                }
                Logging.log("Command " + command + " was not handled!");
            }
        });
    }
}
