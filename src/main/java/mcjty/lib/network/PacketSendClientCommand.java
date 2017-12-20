package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.annotation.Nonnull;

public class PacketSendClientCommand implements IMessage {

    private String modid;
    private String command;
    private Arguments arguments;


    public String getModid() {
        return modid;
    }

    public String getCommand() {
        return command;
    }

    public Arguments getArguments() {
        return arguments;
    }

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

}
