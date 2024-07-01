package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.SafeClientTools;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * This packet is used (typically by PacketRequestDataFromServer) to send back a data to the client.
 */
public record PacketDataFromServer(BlockPos pos, String command, TypedMap result) implements CustomPacketPayload {

    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(McJtyLib.MODID, "datafromserver");
    public static final CustomPacketPayload.Type<PacketDataFromServer> TYPE = new Type<>(ID);

    public static final StreamCodec<FriendlyByteBuf, PacketDataFromServer> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketDataFromServer::pos,
            nullable ByteBufCodecs.STRING_UTF8, PacketDataFromServer::command,   // @todo nullable!!!
            TypedMap.STREAM_CODEC, PacketDataFromServer::result,
            PacketDataFromServer::new
    );

    @Override
    public void write(FriendlyByteBuf buf) {
        if (pos != null) {
            buf.writeBoolean(true);
            buf.writeBlockPos(pos);
        } else {
            buf.writeBoolean(false);
        }
        buf.writeUtf(command);

        buf.writeBoolean(result != null);
        if (result != null) {
            TypedMapTools.writeArguments(buf, result);
        }
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static PacketDataFromServer create(FriendlyByteBuf buf) {
        BlockPos pos;
        if (buf.readBoolean()) {
            pos = buf.readBlockPos();
        } else {
            pos = null;
        }
        String command = buf.readUtf(32767);
        TypedMap result;
        if (buf.readBoolean()) {
            result = TypedMapTools.readArguments(buf);
        } else {
            result = null;
        }
        return new PacketDataFromServer(pos, command, result);
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            BlockEntity te;
            if (pos == null) {
                // We are working from a tablet. Find the tile entity through the open container
                GenericContainer container = getOpenContainer();
                if (container == null) {
                    Logging.log("Container is missing!");
                    return;
                }
                te = container.getTe();
            } else {
                te = SafeClientTools.getClientWorld().getBlockEntity(pos);
            }

            if (te instanceof GenericTileEntity generic) {
                if (generic.executeClientCommand(command, SafeClientTools.getClientPlayer(), result)) {
                    return;
                }
            }

            Logging.log("Command " + command + " was not handled!");
        });
    }

    private static GenericContainer getOpenContainer() {
        AbstractContainerMenu container = SafeClientTools.getClientPlayer().containerMenu;
        if (container instanceof GenericContainer) {
            return (GenericContainer) container;
        } else {
            return null;
        }
    }
}
