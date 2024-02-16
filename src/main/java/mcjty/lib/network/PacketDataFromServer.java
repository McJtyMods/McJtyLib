package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.SafeClientTools;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * This packet is used (typically by PacketRequestDataFromServer) to send back a data to the client.
 */
public record PacketDataFromServer(BlockPos pos, String command, TypedMap result) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(McJtyLib.MODID, "datafromserver");

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
