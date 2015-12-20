package mcjty.lib.network.clientinfo;


import io.netty.buffer.ByteBuf;
import mcjty.lib.network.PacketHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketReturnInfoToClient implements IMessage {

    private InfoPacketClient packet;

    @Override
    public void fromBytes(ByteBuf buf) {
        int id = buf.readInt();
        Class<? extends InfoPacketClient> clazz = PacketHandler.getClientInfoPacket(id);
        try {
            packet = clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        packet.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(PacketHandler.getClientInfoPacketId(packet.getClass()));
        packet.toBytes(buf);
    }

    public InfoPacketClient getPacket() {
        return packet;
    }

    public PacketReturnInfoToClient() {
    }

    public PacketReturnInfoToClient(InfoPacketClient packet) {
        this.packet = packet;
    }

    public static class Handler implements IMessageHandler<PacketReturnInfoToClient, IMessage> {
        @Override
        public IMessage onMessage(PacketReturnInfoToClient message, MessageContext ctx) {
            ReturnInfoHelper.onMessageFromServer(message);
            return null;
        }
    }
}