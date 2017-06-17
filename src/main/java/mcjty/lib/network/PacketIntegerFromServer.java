package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.varia.Logging;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * This packet is used (typically by PacketRequestIntegerFromServer) to send back an integer to the client.
 */
public class PacketIntegerFromServer implements IMessage {
    private BlockPos pos;
    private Integer result;
    private String command;

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = NetworkTools.readPos(buf);

        command = NetworkTools.readString(buf);

        boolean resultPresent = buf.readBoolean();
        if (resultPresent) {
            result = buf.readInt();
        } else {
            result = null;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writePos(buf, pos);

        NetworkTools.writeString(buf, command);

        buf.writeBoolean(result != null);
        if (result != null) {
            buf.writeInt(result);
        }
    }

    public PacketIntegerFromServer() {
    }

    public PacketIntegerFromServer(BlockPos pos, String command, Integer result) {
        this.pos = pos;
        this.command = command;
        this.result = result;
    }

    public static class Handler implements IMessageHandler<PacketIntegerFromServer, IMessage> {
        @Override
        public IMessage onMessage(PacketIntegerFromServer message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketIntegerFromServer message, MessageContext ctx) {
            TileEntity te = Minecraft.getMinecraft().world.getTileEntity(message.pos);
            if(!(te instanceof ClientCommandHandler)) {
                Logging.log("createInventoryReadyPacket: TileEntity is not a ClientCommandHandler!");
                return;
            }
            ClientCommandHandler clientCommandHandler = (ClientCommandHandler) te;
            if (!clientCommandHandler.execute(message.command, message.result)) {
                Logging.log("Command " + message.command + " was not handled!");
            }
        }

    }
}
