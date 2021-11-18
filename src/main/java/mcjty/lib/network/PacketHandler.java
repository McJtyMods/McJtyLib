package mcjty.lib.network;

import mcjty.lib.syncpositional.PacketSendPositionalDataToClients;
import mcjty.lib.typed.TypedMap;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import javax.annotation.Nonnull;

public class PacketHandler {

    public static boolean connected = false;

    // Only use client-side!
    private static <MSG> boolean canBeSent(MSG message) {
        return connected;
    }

    // Only use client-side!
    public static void onDisconnect() {
        connected = false;
    }


    public static void registerMessages(SimpleChannel channel) {
        int startIndex = 0;
        channel.registerMessage(startIndex++, PacketSendPreferencesToClient.class, PacketSendPreferencesToClient::toBytes, PacketSendPreferencesToClient::new, PacketSendPreferencesToClient::handle);
        channel.registerMessage(startIndex++, PacketSetGuiStyle.class, PacketSetGuiStyle::toBytes, PacketSetGuiStyle::new, PacketSetGuiStyle::handle);
        channel.registerMessage(startIndex++, PacketOpenManual.class, PacketOpenManual::toBytes, PacketOpenManual::new, PacketOpenManual::handle);
        channel.registerMessage(startIndex++, PacketContainerDataToClient.class, PacketContainerDataToClient::toBytes, PacketContainerDataToClient::new, PacketContainerDataToClient::handle);
        channel.registerMessage(startIndex++, PacketSendPositionalDataToClients.class, PacketSendPositionalDataToClients::toBytes, PacketSendPositionalDataToClients::new, PacketSendPositionalDataToClients::handle);
    }

    public static void registerStandardMessages(int id, SimpleChannel channel) {

        // Server side
        channel.registerMessage(id++, PacketGetListFromServer.class, PacketGetListFromServer::toBytes, PacketGetListFromServer::new, PacketGetListFromServer::handle);
        channel.registerMessage(id++, PacketServerCommandTyped.class, PacketServerCommandTyped::toBytes, PacketServerCommandTyped::new, PacketServerCommandTyped::handle);
        channel.registerMessage(id++, PacketSendServerCommand.class, PacketSendServerCommand::toBytes, PacketSendServerCommand::new, PacketSendServerCommand::handle);
        channel.registerMessage(id++, PacketDumpItemInfo.class, PacketDumpItemInfo::toBytes, PacketDumpItemInfo::new, PacketDumpItemInfo::handle);
        channel.registerMessage(id++, PacketDumpBlockInfo.class, PacketDumpBlockInfo::toBytes, PacketDumpBlockInfo::new, PacketDumpBlockInfo::handle);

        // Client side
        channel.registerMessage(id++, PacketSendClientCommand.class, PacketSendClientCommand::toBytes, PacketSendClientCommand::new, PacketSendClientCommand::handle);
        channel.registerMessage(id++, PacketDataFromServer.class, PacketDataFromServer::toBytes, PacketDataFromServer::new, PacketDataFromServer::handle);
        channel.registerMessage(id++, PacketFinalizeLogin.class, PacketFinalizeLogin::toBytes, PacketFinalizeLogin::new, PacketFinalizeLogin::handle);
        channel.registerMessage(id++, PacketSendResultToClient.class, PacketSendResultToClient::toBytes, PacketSendResultToClient::new, PacketSendResultToClient::handle);
    }

    // From client side only: send server command
    public static void sendCommand(SimpleChannel network, String modid, String command, @Nonnull TypedMap arguments) {
        network.sendToServer(new PacketSendServerCommand(modid, command, arguments));
    }

}
