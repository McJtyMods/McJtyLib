package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.blockcommands.CommandInfo;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.SafeClientTools;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Packet to send back the list to the client. This requires
 * that the command is registered to McJtyLib.registerListCommandInfo
 */
public record PacketSendResultToClient(BlockPos pos, String command, List list) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(McJtyLib.MODID, "sendresulttoclient");
    public static final CustomPacketPayload.Type<PacketSendResultToClient> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PacketSendResultToClient> CODEC = StreamCodec.of(
            (buf, packet) -> {
                buf.writeBlockPos(packet.pos);
                buf.writeUtf(packet.command);
                CommandInfo<?> info = McJtyLib.getCommandInfo(packet.command);
                if (info == null) {
                    throw new IllegalStateException("Command '" + packet.command + "' is not registered!");
                }
                BiConsumer<FriendlyByteBuf, Object> serializer = (BiConsumer<FriendlyByteBuf, Object>) info.serializer();
                if (serializer == null) {
                    throw new IllegalStateException("Command '" + packet.command + "' is not registered!");
                }
                if (packet.list == null) {
                    buf.writeInt(-1);
                } else {
                    buf.writeInt(packet.list.size());
                    for (Object item : packet.list) {
                        serializer.accept(buf, item);
                    }
                }
            },
            buf -> {
                BlockPos pos = buf.readBlockPos();
                String command = buf.readUtf(32767);
                CommandInfo<?> info = McJtyLib.getCommandInfo(command);
                if (info == null) {
                    throw new IllegalStateException("Command '" + command + "' is not registered!");
                }
                Function<FriendlyByteBuf, ?> deserializer = info.deserializer();
                int size = buf.readInt();
                List list;
                if (size != -1) {
                    list = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) {
                        list.add(deserializer.apply(buf));
                    }
                } else {
                    list = null;
                }
                return new PacketSendResultToClient(pos, command, list);
            });

    public static PacketSendResultToClient create(BlockPos pos, String command, List list) {
        return new PacketSendResultToClient(pos, command, new ArrayList(list));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            BlockEntity te = SafeClientTools.getClientWorld().getBlockEntity(pos);
            if (te instanceof GenericTileEntity generic) {
                generic.handleListFromServer(command, SafeClientTools.getClientPlayer(), TypedMap.EMPTY, list);
            } else {
                Logging.logError("Can't handle command '" + command + "'!");
            }
        });
    }

}
