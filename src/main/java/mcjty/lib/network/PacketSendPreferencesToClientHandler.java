package mcjty.lib.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSendPreferencesToClientHandler implements IMessageHandler<PacketSendPreferencesToClient, IMessage> {
    @Override
    public IMessage onMessage(PacketSendPreferencesToClient message, MessageContext ctx) {
        SendPreferencesToClientHelper.setPreferences(message);
        return null;
    }

}
