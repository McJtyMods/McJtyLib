package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.typed.TypedMap;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class PacketSendClientCommand {

    private String modid;
    private String command;
    private TypedMap arguments;


    public String getModid() {
        return modid;
    }

    public String getCommand() {
        return command;
    }

    public TypedMap getArguments() {
        return arguments;
    }

    public void toBytes(ByteBuf buf) {
        NetworkTools.writeString(buf, modid);
        NetworkTools.writeString(buf, command);
        TypedMapTools.writeArguments(buf, arguments);
    }

    public PacketSendClientCommand(ByteBuf buf) {
        modid = NetworkTools.readString(buf);
        command = NetworkTools.readString(buf);
        arguments = TypedMapTools.readArguments(buf);
    }

    public PacketSendClientCommand(String modid, String command, @Nonnull TypedMap arguments) {
        this.modid = modid;
        this.command = command;
        this.arguments = arguments;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            ClientCommandHandlerHelper.handle(this);
        });
        ctx.setPacketHandled(true);
    }
}
