package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.varia.Logging;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSendClientCommandHandler implements IMessageHandler<PacketSendClientCommand, IMessage> {

    @Override
    public IMessage onMessage(PacketSendClientCommand message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> handle(message, ctx));
        return null;
    }

    private void handle(PacketSendClientCommand message, MessageContext ctx) {
        boolean result = McJtyLib.handleClientCommand(message.getModid(), message.getCommand(), Minecraft.getMinecraft().player, message.getArguments());
        if (!result) {
            Logging.logError("Error handling client command '" + message.getCommand() + "' for mod '" + message.getModid() + "'!");
        }
    }
}
