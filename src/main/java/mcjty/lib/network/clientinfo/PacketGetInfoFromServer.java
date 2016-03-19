package mcjty.lib.network.clientinfo;


import io.netty.buffer.ByteBuf;
import mcjty.lib.network.NetworkTools;
import mcjty.lib.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class PacketGetInfoFromServer implements IMessage {

    private InfoPacketServer packet;
    private String modid;

    @Override
    public void fromBytes(ByteBuf buf) {
        int id = buf.readInt();
        Class<? extends InfoPacketServer> clazz = PacketHandler.getServerInfoPacket(id);
        try {
            packet = clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        packet.fromBytes(buf);
        modid = NetworkTools.readString(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(PacketHandler.getServerInfoPacketId(packet.getClass()));
        packet.toBytes(buf);
        NetworkTools.writeString(buf, modid);
    }

    public PacketGetInfoFromServer() {
    }

    public PacketGetInfoFromServer(String modid, InfoPacketServer packet) {
        this.packet = packet;
        this.modid = modid;
    }

    public static class Handler implements IMessageHandler<PacketGetInfoFromServer, IMessage> {
        @Override
        public IMessage onMessage(PacketGetInfoFromServer message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(()
                    -> message.packet.onMessageServer(ctx.getServerHandler().playerEntity)
                    .ifPresent(p -> sendReplyToClient(message.modid, p, ctx.getServerHandler().playerEntity)));
            return null;
        }

        private void sendReplyToClient(String modid, InfoPacketClient reply, EntityPlayerMP player) {
            SimpleNetworkWrapper wrapper = PacketHandler.modNetworking.get(modid);
            PacketReturnInfoToClient msg = new PacketReturnInfoToClient(reply);
            wrapper.sendTo(msg, player);
        }
    }
}
