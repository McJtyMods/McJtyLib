package mcjty.lib.network;

import io.netty.buffer.ByteBuf;
import mcjty.lib.McJtyLib;
import mcjty.lib.thirteen.Context;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class PacketSendServerCommand implements IMessage {

    private String modid;
    private String command;
    private TypedMap arguments;

    @Override
    public void fromBytes(ByteBuf buf) {
        modid = NetworkTools.readString(buf);
        command = NetworkTools.readString(buf);
        arguments = TypedMapTools.readArguments(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkTools.writeString(buf, modid);
        NetworkTools.writeString(buf, command);
        TypedMapTools.writeArguments(buf, arguments);
    }

    public PacketSendServerCommand() {
    }

    public PacketSendServerCommand(ByteBuf buf) {
        fromBytes(buf);
    }

    public PacketSendServerCommand(String modid, String command, @Nonnull TypedMap arguments) {
        this.modid = modid;
        this.command = command;
        this.arguments = arguments;
    }

    public void handle(Supplier<Context> supplier) {
        Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            boolean result = McJtyLib.handleCommand(modid, command, ctx.getSender(), arguments);
            if (!result) {
                Logging.logError("Error handling command '" + command + "' for mod '" + modid + "'!");
            }
        });
        ctx.setPacketHandled(true);
    }
}
