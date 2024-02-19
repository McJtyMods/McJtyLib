package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.blockcommands.ICommand;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.LevelTools;
import mcjty.lib.varia.Logging;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

/**
 * This is a packet that can be used to send a command from the client side (typically the GUI) to
 * a tile entity on the server that has a ResultCommand annotated with @ServerCommand
 */
public record PacketRequestDataFromServer(ResourceKey<Level> type, BlockPos pos, String command, TypedMap params, boolean dummy) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(McJtyLib.MODID, "requestdatafromserver");

    public static PacketRequestDataFromServer create(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        ResourceKey<Level> type = LevelTools.getId(buf.readResourceLocation());
        String command = buf.readUtf(32767);
        TypedMap params = TypedMapTools.readArguments(buf);
        boolean dummy = buf.readBoolean();
        return new PacketRequestDataFromServer(type, pos, command, params, dummy);
    }

    public static PacketRequestDataFromServer create(ResourceKey<Level> type, BlockPos pos, String command, TypedMap params, boolean dummy) {
        return new PacketRequestDataFromServer(type, pos, command, params, dummy);
    }

    public static PacketRequestDataFromServer create(ResourceKey<Level> type, BlockPos pos, ICommand command, TypedMap params, boolean dummy) {
        return new PacketRequestDataFromServer(type, pos, command.name(), params, dummy);
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeResourceLocation(type.location());
        buf.writeUtf(command);
        TypedMapTools.writeArguments(buf, params);
        buf.writeBoolean(dummy);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ctx.player().ifPresent(player -> {
                Level world = LevelTools.getLevel(player.getCommandSenderWorld(), type);
                if (world.hasChunkAt(pos)) {
                    if (world.getBlockEntity(pos) instanceof GenericTileEntity generic) {
                        TypedMap result = generic.executeServerCommandWR(command, player, params);
                        if (result != null) {
                            PacketDataFromServer msg = new PacketDataFromServer(dummy ? null : pos, command, result);
                            McJtyLib.sendToPlayer(msg, player);
                            return;
                        }
                    }

                    Logging.log("Command " + command + " was not handled!");
                }
            });
        });
    }
}
