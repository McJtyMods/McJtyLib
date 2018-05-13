package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import mcjty.lib.typed.TypedMap;
import mcjty.lib.varia.Logging;
import net.minecraft.client.Minecraft;

public class ClientCommandHandlerHelper {

    static void onMessage(PacketSendClientCommand message) {
        Minecraft.getMinecraft().addScheduledTask(() -> handle(message));
    }

    private static void handle(PacketSendClientCommand message) {
        String modid = message.getModid();
        String command = message.getCommand();
        TypedMap arguments = message.getArguments();
        handleClientCommand(modid, command, arguments);
    }

    private static void handleClientCommand(String modid, String command, TypedMap arguments) {
        boolean result = McJtyLib.handleClientCommand(modid, command, Minecraft.getMinecraft().player, arguments);
        if (!result) {
            Logging.logError("Error handling client command '" + command + "' for mod '" + modid + "'!");
        }
    }
}
