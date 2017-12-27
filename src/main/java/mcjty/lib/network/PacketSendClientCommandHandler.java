package mcjty.lib.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSendClientCommandHandler implements IMessageHandler<PacketSendClientCommand, IMessage> {

    @Override
    public IMessage onMessage(PacketSendClientCommand message, MessageContext ctx) {
        ClientCommandHandlerHelper.onMessage(message);
        return null;
    }

}
