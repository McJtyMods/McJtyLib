package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.McJtyLib;
import mcjty.lib.varia.Logging;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;

public class PacketSendClientCommand implements IMessage {

    private String modid;
    private String command;
    private Arguments arguments;

    @Override
    public void fromBytes(ByteBuf buf) {
        modid = NetworkTools.readString(buf);
        command = NetworkTools.readString(buf);
        arguments = new Arguments(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writeString(buf, modid);
        NetworkTools.writeString(buf, command);
        arguments.toBytes(buf);
    }

    public PacketSendClientCommand() {
    }

    public PacketSendClientCommand(String modid, String command, @Nonnull Arguments arguments) {
        this.modid = modid;
        this.command = command;
        this.arguments = arguments;
    }

    public static class Handler implements IMessageHandler<PacketSendClientCommand, IMessage> {
        @Override
        public IMessage onMessage(PacketSendClientCommand message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketSendClientCommand message, MessageContext ctx) {
            boolean result = McJtyLib.handleClientCommand(message.modid, message.command, Minecraft.getMinecraft().player, message.arguments);
            if (!result) {
                Logging.logError("Error handling client command '" + message.command + "' for mod '" + message.modid + "'!");
            }
        }
    }
}
