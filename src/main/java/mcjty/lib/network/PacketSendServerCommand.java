package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.McJtyLib;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class PacketSendServerCommand {

    private String modid;
    private String command;
    private TypedMap arguments;

    public void toBytes(ByteBuf buf) {
        NetworkTools.writeString(buf, modid);
        NetworkTools.writeString(buf, command);
        TypedMapTools.writeArguments(buf, arguments);
    }

    public PacketSendServerCommand(ByteBuf buf) {
        modid = NetworkTools.readString(buf);
        command = NetworkTools.readString(buf);
        arguments = TypedMapTools.readArguments(buf);
    }

    public PacketSendServerCommand(String modid, String command, @Nonnull TypedMap arguments) {
        this.modid = modid;
        this.command = command;
        this.arguments = arguments;
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            boolean result = McJtyLib.handleCommand(modid, command, ctx.getSender(), arguments);
            if (!result) {
                Logging.logError("Error handling command '" + command + "' for mod '" + modid + "'!");
            }
        });
        ctx.setPacketHandled(true);
    }
}
