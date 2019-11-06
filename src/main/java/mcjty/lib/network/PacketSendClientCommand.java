package mcjty.lib.network;

import mcjty.lib.typed.TypedMap;
import net.minecraft.network.PacketBuffer;
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

    public void toBytes(PacketBuffer buf) {
        buf.writeString(modid);
        buf.writeString(command);
        TypedMapTools.writeArguments(buf, arguments);
    }

    public PacketSendClientCommand(PacketBuffer buf) {
        modid = buf.readString();
        command = buf.readString();
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
