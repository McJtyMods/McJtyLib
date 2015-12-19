package mcjty.lib.network.clientinfo;


import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketReturnInfoHandler implements IMessageHandler<PacketReturnInfoToClient, IMessage> {
    @Override
    public IMessage onMessage(PacketReturnInfoToClient message, MessageContext ctx) {
        ReturnInfoHelper.onMessageFromServer(message);
        return null;
    }

}