package mcjty.lib.network;

import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Supplier;

/**
 * This is a packet that can be used to send a command from the client side (typically the GUI) to
 * a tile entity on the server side that implements CommandHandler. This will call 'executeWithResultInteger()' on
 * that command handler. A PacketIntegerFromServer will be sent back from the client.
 */
public class PacketRequestDataFromServer {
    protected BlockPos pos;
    protected String command;
    protected TypedMap params;
    private String modid;

    public PacketRequestDataFromServer(PacketBuffer buf) {
        pos = NetworkTools.readPos(buf);
        command = NetworkTools.readString(buf);
        params = TypedMapTools.readArguments(buf);
        modid = NetworkTools.readString(buf);
    }

    public void toBytes(PacketBuffer buf) {
        NetworkTools.writePos(buf, pos);
        NetworkTools.writeString(buf, command);
        TypedMapTools.writeArguments(buf, params);
        NetworkTools.writeString(buf, modid);
    }

    public PacketRequestDataFromServer(String modid, BlockPos pos, String command, TypedMap params) {
        this.pos = pos;
        this.command = command;
        this.params = params;
        this.modid = modid;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            TileEntity te = ctx.getSender().getEntityWorld().getTileEntity(pos);
            if(!(te instanceof ICommandHandler)) {
                Logging.log("createStartScanPacket: TileEntity is not a CommandHandler!");
                return;
            }
            ICommandHandler commandHandler = (ICommandHandler) te;
            TypedMap result = commandHandler.executeWithResult(command, params);
            if (result == null) {
                Logging.log("Command " + command + " was not handled!");
                return;
            }

            sendReplyToClient(this, result, ctx.getSender());
        });
        ctx.setPacketHandled(true);
    }

    private void sendReplyToClient(PacketRequestDataFromServer message, TypedMap result, PlayerEntity player) {
        SimpleChannel wrapper = PacketHandler.modNetworking.get(message.modid);
        PacketDataFromServer msg = new PacketDataFromServer(message.pos, message.command, result);
        wrapper.sendTo(msg, ((ServerPlayerEntity) player).connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
    }
}
