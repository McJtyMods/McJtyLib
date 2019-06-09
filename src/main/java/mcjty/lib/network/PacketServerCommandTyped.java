package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * This is a packet that can be used to send a command from the client side (typically the GUI) to
 * a tile entity on the server side that implements CommandHandler. This will call 'execute()' on
 * that command handler.
 */
public class PacketServerCommandTyped {

    protected BlockPos pos;
    protected Integer dimensionId;
    protected String command;
    protected TypedMap params;

    public PacketServerCommandTyped(ByteBuf buf) {
        pos = NetworkTools.readPos(buf);
        command = NetworkTools.readString(buf);
        params = TypedMapTools.readArguments(buf);
        if (buf.readBoolean()) {
            dimensionId = buf.readInt();
        } else {
            dimensionId = null;
        }
    }

    public PacketServerCommandTyped(BlockPos pos, String command, TypedMap params) {
        this.pos = pos;
        this.command = command;
        this.params = params;
        this.dimensionId = null;
    }

    public PacketServerCommandTyped(BlockPos pos, Integer dimensionId, String command, TypedMap params) {
        this.pos = pos;
        this.command = command;
        this.params = params;
        this.dimensionId = dimensionId;
    }

    public void toBytes(ByteBuf buf) {
        NetworkTools.writePos(buf, pos);
        NetworkTools.writeString(buf, command);
        TypedMapTools.writeArguments(buf, params);
        if (dimensionId != null) {
            buf.writeBoolean(true);
            buf.writeInt(dimensionId);
        } else {
            buf.writeBoolean(false);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            PlayerEntity playerEntity = ctx.getSender();
            World world;
            if (dimensionId == null) {
                world = playerEntity.getEntityWorld();
            } else {
                world = DimensionManager.getWorld(playerEntity.getServer(), DimensionType.getById(dimensionId), false, false);  // @todo 1.14 check?
            }
            if (world == null) {
                return;
            }
            TileEntity te = world.getTileEntity(pos);
            if(!(te instanceof ICommandHandler)) {
                Logging.log("createStartScanPacket: TileEntity is not a CommandHandler!");
                return;
            }
            ICommandHandler commandHandler = (ICommandHandler) te;
            if (!commandHandler.execute(playerEntity, command, params)) {
                Logging.log("Command " + command + " was not handled!");
            }
        });
        ctx.setPacketHandled(true);
    }
}
