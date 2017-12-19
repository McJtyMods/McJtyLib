package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.McJtyLib;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nonnull;

public class PacketSendServerCommand implements IMessage {

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

    public PacketSendServerCommand() {
    }

    public PacketSendServerCommand(String modid, String command, @Nonnull Arguments arguments) {
        this.modid = modid;
        this.command = command;
        this.arguments = arguments;
    }

    public static class Handler implements IMessageHandler<PacketSendServerCommand, IMessage> {
        @Override
        public IMessage onMessage(PacketSendServerCommand message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketSendServerCommand message, MessageContext ctx) {
            McJtyLib.handleCommand(message.modid, message.command, ctx.getServerHandler().player, message.arguments);
        }
    }
}
