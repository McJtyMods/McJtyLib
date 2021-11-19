package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Send a packet from the client to the server in order to execute a server side command
 * registered with McJtyLib.registerCommand()
 */
public class PacketSendServerCommand {

    // Package visible for unit tests
    String modid;
    String command;
    TypedMap arguments;

    public void toBytes(PacketBuffer buf) {
        buf.writeUtf(modid);
        buf.writeUtf(command);
        TypedMapTools.writeArguments(buf, arguments);
    }

    public PacketSendServerCommand(PacketBuffer buf) {
        modid = buf.readUtf(32767);
        command = buf.readUtf(32767);
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
