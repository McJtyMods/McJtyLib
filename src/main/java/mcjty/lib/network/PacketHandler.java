package mcjty.lib.network;

import mcjty.lib.syncpositional.PacketSendPositionalDataToClients;
import mcjty.lib.typed.TypedMap;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nonnull;

import static mcjty.lib.network.PlayPayloadContext.wrap;

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
        channel.registerMessage(startIndex++, PacketSendPreferencesToClient.class, PacketSendPreferencesToClient::write, PacketSendPreferencesToClient::create, wrap(PacketSendPreferencesToClient::handle));
        channel.registerMessage(startIndex++, PacketSetGuiStyle.class, PacketSetGuiStyle::write, PacketSetGuiStyle::create, wrap(PacketSetGuiStyle::handle));
        channel.registerMessage(startIndex++, PacketOpenManual.class, PacketOpenManual::write, PacketOpenManual::create, wrap(PacketOpenManual::handle));
        channel.registerMessage(startIndex++, PacketContainerDataToClient.class, PacketContainerDataToClient::write, PacketContainerDataToClient::create, wrap(PacketContainerDataToClient::handle));
        channel.registerMessage(startIndex++, PacketSendPositionalDataToClients.class, PacketSendPositionalDataToClients::write, PacketSendPositionalDataToClients::create, wrap(PacketSendPositionalDataToClients::handle));
        channel.registerMessage(startIndex++, PacketSendResultToClient.class, PacketSendResultToClient::write, PacketSendResultToClient::create, wrap(PacketSendResultToClient::handle));
    }

    public static void registerStandardMessages(int id, SimpleChannel channel) {

        // Server side
        channel.registerMessage(id++, PacketGetListFromServer.class, PacketGetListFromServer::write, PacketGetListFromServer::create, wrap(PacketGetListFromServer::handle));
        channel.registerMessage(id++, PacketServerCommandTyped.class, PacketServerCommandTyped::write, PacketServerCommandTyped::create, wrap(PacketServerCommandTyped::handle));
        channel.registerMessage(id++, PacketSendServerCommand.class, PacketSendServerCommand::write, PacketSendServerCommand::create, wrap(PacketSendServerCommand::handle));
        channel.registerMessage(id++, PacketDumpItemInfo.class, PacketDumpItemInfo::write, PacketDumpItemInfo::create, wrap(PacketDumpItemInfo::handle));
        channel.registerMessage(id++, PacketDumpBlockInfo.class, PacketDumpBlockInfo::write, PacketDumpBlockInfo::create, wrap(PacketDumpBlockInfo::handle));

        // Client side
        channel.registerMessage(id++, PacketSendClientCommand.class, PacketSendClientCommand::write, PacketSendClientCommand::create, wrap(PacketSendClientCommand::handle));
        channel.registerMessage(id++, PacketDataFromServer.class, PacketDataFromServer::write, PacketDataFromServer::create, wrap(PacketDataFromServer::handle));
        channel.registerMessage(id++, PacketFinalizeLogin.class, PacketFinalizeLogin::write, PacketFinalizeLogin::create, wrap(PacketFinalizeLogin::handle));
    }

    // From client side only: send server command
    public static void sendCommand(SimpleChannel network, String modid, String command, @Nonnull TypedMap arguments) {
        network.sendToServer(new PacketSendServerCommand(modid, command, arguments));
    }

}
