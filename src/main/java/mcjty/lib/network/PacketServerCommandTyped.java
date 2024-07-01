package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.LevelTools;
import mcjty.lib.varia.Logging;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

/**
 * This is a packet that can be used to send a command from the client side (typically the GUI) to
 * a tile entity on the server side that implements CommandHandler. This will call 'execute()' on
 * that command handler.
 */
public record PacketServerCommandTyped(BlockPos pos, ResourceKey<Level> dimensionId, String command, TypedMap params) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(McJtyLib.MODID, "servercommandtyped");

    public static PacketServerCommandTyped create(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        ResourceKey<Level> dimensionId;
        if (buf.readBoolean()) {
            dimensionId = LevelTools.getId(buf.readResourceLocation());
        } else {
            dimensionId = null;
        }
        String command = buf.readUtf(32767);
        TypedMap params = TypedMapTools.readArguments(buf);
        return new PacketServerCommandTyped(pos, dimensionId, command, params);
    }

    public static PacketServerCommandTyped create(BlockPos blockPos, ResourceKey<Level> dimension, String command, TypedMap params) {
        return new PacketServerCommandTyped(blockPos, dimension, command, params);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        if (dimensionId != null) {
            buf.writeBoolean(true);
            buf.writeResourceLocation(dimensionId.location());
        } else {
            buf.writeBoolean(false);
        }
        buf.writeUtf(command);
        TypedMapTools.writeArguments(buf, params);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ctx.player().ifPresent(playerEntity -> {
                Level world;
                if (dimensionId == null) {
                    world = playerEntity.getCommandSenderWorld();
                } else {
                    world = LevelTools.getLevel(playerEntity.level(), dimensionId);
                }
                if (world == null) {
                    return;
                }
                if (world.hasChunkAt(pos)) {
                    if (world.getBlockEntity(pos) instanceof GenericTileEntity generic) {
                        if (generic.executeServerCommand(command, playerEntity, params)) {
                            return;
                        }
                    }
                    Logging.log("Command " + command + " was not handled!");
                }
            });
        });
    }
}
