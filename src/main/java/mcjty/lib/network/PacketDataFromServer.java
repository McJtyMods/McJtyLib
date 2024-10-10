package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import mcjty.lib.varia.SafeClientTools;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

/**
 * This packet is used (typically by PacketRequestDataFromServer) to send back a data to the client.
 */
public record PacketDataFromServer(BlockPos pos, String command, TypedMap result) implements CustomPacketPayload {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(McJtyLib.MODID, "datafromserver");
    public static final CustomPacketPayload.Type<PacketDataFromServer> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketDataFromServer> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC.apply(ByteBufCodecs::optional), s -> Optional.ofNullable(s.pos),
            ByteBufCodecs.STRING_UTF8, PacketDataFromServer::command,
            TypedMap.STREAM_CODEC, PacketDataFromServer::result,
            (pos, command, result) -> new PacketDataFromServer(pos.orElse(null), command, result)
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            BlockEntity te;
            if (pos == null) {
                // We are working from a tablet. Find the be entity through the open container
                GenericContainer container = getOpenContainer();
                if (container == null) {
                    Logging.log("Container is missing!");
                    return;
                }
                te = container.getBe();
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
