package mcjty.lib.network;

import net.minecraftforge.network.simple.SimpleChannel;

import static mcjty.lib.network.PlayPayloadContext.wrap;

public class PacketHandler {

    public static void registerMessages(SimpleChannel channel) {
        int startIndex = 0;
        channel.registerMessage(startIndex++, PacketSendPreferencesToClient.class, PacketSendPreferencesToClient::write, PacketSendPreferencesToClient::create, wrap(PacketSendPreferencesToClient::handle));
        channel.registerMessage(startIndex++, PacketSetGuiStyle.class, PacketSetGuiStyle::write, PacketSetGuiStyle::create, wrap(PacketSetGuiStyle::handle));
        channel.registerMessage(startIndex++, PacketOpenManual.class, PacketOpenManual::write, PacketOpenManual::create, wrap(PacketOpenManual::handle));
        channel.registerMessage(startIndex++, PacketContainerDataToClient.class, PacketContainerDataToClient::write, PacketContainerDataToClient::create, wrap(PacketContainerDataToClient::handle));
        channel.registerMessage(startIndex++, PacketSendResultToClient.class, PacketSendResultToClient::write, PacketSendResultToClient::create, wrap(PacketSendResultToClient::handle));


        // Server side
        channel.registerMessage(startIndex++, PacketGetListFromServer.class, PacketGetListFromServer::write, PacketGetListFromServer::create, wrap(PacketGetListFromServer::handle));
        channel.registerMessage(startIndex++, PacketServerCommandTyped.class, PacketServerCommandTyped::write, PacketServerCommandTyped::create, wrap(PacketServerCommandTyped::handle));
        channel.registerMessage(startIndex++, PacketSendServerCommand.class, PacketSendServerCommand::write, PacketSendServerCommand::create, wrap(PacketSendServerCommand::handle));
        channel.registerMessage(startIndex++, PacketRequestDataFromServer.class, PacketRequestDataFromServer::write, PacketRequestDataFromServer::create, wrap(PacketRequestDataFromServer::handle));

        // Client side
        channel.registerMessage(startIndex++, PacketSendClientCommand.class, PacketSendClientCommand::write, PacketSendClientCommand::create, wrap(PacketSendClientCommand::handle));
        channel.registerMessage(startIndex++, PacketDataFromServer.class, PacketDataFromServer::write, PacketDataFromServer::create, wrap(PacketDataFromServer::handle));
        channel.registerMessage(startIndex++, PacketFinalizeLogin.class, PacketFinalizeLogin::write, PacketFinalizeLogin::create, wrap(PacketFinalizeLogin::handle));
    }
}