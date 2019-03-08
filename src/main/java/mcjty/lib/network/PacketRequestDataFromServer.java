package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.thirteen.Context;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import java.util.function.Supplier;

/**
 * This is a packet that can be used to send a command from the client side (typically the GUI) to
 * a tile entity on the server side that implements CommandHandler. This will call 'executeWithResultInteger()' on
 * that command handler. A PacketIntegerFromServer will be sent back from the client.
 */
public class PacketRequestDataFromServer implements IMessage {
    protected BlockPos pos;
    protected String command;
    protected TypedMap params;
    private String modid;

    public PacketRequestDataFromServer() {
    }

    public PacketRequestDataFromServer(ByteBuf buf) {
        fromBytes(buf);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = NetworkTools.readPos(buf);
        command = NetworkTools.readString(buf);
        params = TypedMapTools.readArguments(buf);
        modid = NetworkTools.readString(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
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

    public void handle(Supplier<Context> supplier) {
        Context ctx = supplier.get();
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

    private void sendReplyToClient(PacketRequestDataFromServer message, TypedMap result, EntityPlayerMP player) {
        SimpleNetworkWrapper wrapper = PacketHandler.modNetworking.get(message.modid);
        PacketDataFromServer msg = new PacketDataFromServer(message.pos, message.command, result);
        wrapper.sendTo(msg, player);
    }
}
