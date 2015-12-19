package mcjty.lib.network.clientinfo;


import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import mcjty.lib.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGetInfoFromServer implements IMessage {

    private InfoPacketServer packet;

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
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(PacketHandler.getServerInfoPacketId(packet.getClass()));
        packet.toBytes(buf);
    }

    public PacketGetInfoFromServer() {
    }

    public PacketGetInfoFromServer(InfoPacketServer packet) {
        this.packet = packet;
    }

    public static class Handler implements IMessageHandler<PacketGetInfoFromServer, IMessage> {
        @Override
        public IMessage onMessage(PacketGetInfoFromServer message, MessageContext ctx) {
            MinecraftServer.getServer().addScheduledTask(()
                    -> message.packet.onMessageServer(ctx.getServerHandler().playerEntity)
                    .ifPresent(p -> sendReplyToClient(ctx.getServerHandler().getNetworkManager(), p, ctx.getServerHandler().playerEntity)));
            return null;
        }

        private void sendReplyToClient(NetworkManager network, InfoPacketClient reply, EntityPlayerMP player) {
            Channel channel = network.channel();
            channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
            channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
            channel.writeAndFlush(new PacketReturnInfoToClient(reply)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
//            network.sendTo(new PacketReturnInfoToClient(reply), player);
        }
    }
}
