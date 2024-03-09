package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

/**
 * Send a packet from the client to the server in order to execute a server side command
 * registered with McJtyLib.registerCommand()
 */
public record PacketSendServerCommand(String modid, String command, TypedMap arguments) implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(McJtyLib.MODID, "sendservercommand");

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(modid);
        buf.writeUtf(command);
        TypedMapTools.writeArguments(buf, arguments);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static PacketSendServerCommand create(String modid, String command, TypedMap arguments) {
        return new PacketSendServerCommand(modid, command, arguments);
    }

    public static PacketSendServerCommand create(FriendlyByteBuf buf) {
        String modid = buf.readUtf(32767);
        String command = buf.readUtf(32767);
        TypedMap arguments = TypedMapTools.readArguments(buf);
        return new PacketSendServerCommand(modid, command, arguments);
    }

    public void handle(PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> {
            ctx.player().ifPresent(player -> {
                try {
                    boolean result = McJtyLib.handleCommand(modid, command, player, arguments);
                    if (!result) {
                        Logging.logError("Error handling command '" + command + "' for mod '" + modid + "'!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
        });
    }
}
