package mcjty.lib.network;

import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.LevelTools;
import mcjty.lib.varia.Logging;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * This is a packet that can be used to send a command from the client side (typically the GUI) to
 * a tile entity on the server side that implements CommandHandler. This will call 'execute()' on
 * that command handler.
 */
public class PacketServerCommandTyped {

    protected BlockPos pos;
    protected ResourceKey<Level> dimensionId;
    protected String command;
    protected TypedMap params;

    public PacketServerCommandTyped(FriendlyByteBuf buf) {
        pos = buf.readBlockPos();
        command = buf.readUtf(32767);
        params = TypedMapTools.readArguments(buf);
        if (buf.readBoolean()) {
            dimensionId = LevelTools.getId(buf.readResourceLocation());
        } else {
            dimensionId = null;
        }
    }

    public PacketServerCommandTyped(BlockPos pos, ResourceKey<Level> dimensionId, String command, TypedMap params) {
        this.pos = pos;
        this.command = command;
        this.params = params;
        this.dimensionId = dimensionId;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeUtf(command);
        TypedMapTools.writeArguments(buf, params);
        if (dimensionId != null) {
            buf.writeBoolean(true);
            buf.writeResourceLocation(dimensionId.location());
        } else {
            buf.writeBoolean(false);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Player playerEntity = ctx.getSender();
            Level world;
            if (dimensionId == null) {
                world = playerEntity.getCommandSenderWorld();
            } else {
                world = LevelTools.getLevel(playerEntity.level, dimensionId);
            }
            if (world == null) {
                return;
            }
            if (world.hasChunkAt(pos)) {
                BlockEntity te = world.getBlockEntity(pos);
                if (te instanceof GenericTileEntity) {
                    if (((GenericTileEntity) te).executeServerCommand(command, playerEntity, params)) {
                        return;
                    }
                }
                Logging.log("Command " + command + " was not handled!");
            }
        });
        ctx.setPacketHandled(true);
    }
}
