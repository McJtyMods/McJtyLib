package mcjty.lib.network;

import mcjty.lib.typed.TypedMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

@SuppressWarnings("ALL")
public class PacketSendClientCommand {

    // Package visible for unit tests
    String modid;
    String command;
    TypedMap arguments;


    public String getModid() {
        return modid;
    }

    public String getCommand() {
        return command;
    }

    public TypedMap getArguments() {
        return arguments;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(modid);
        buf.writeUtf(command);
        TypedMapTools.writeArguments(buf, arguments);
    }

    public PacketSendClientCommand(FriendlyByteBuf buf) {
        modid = buf.readUtf(32767);
        command = buf.readUtf(32767);
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
