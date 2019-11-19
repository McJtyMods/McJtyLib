package mcjty.lib.network;

import mcjty.lib.typed.TypedMap;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import javax.annotation.Nonnull;

public class PacketHandler {

    /* Make sure this number is higher than the amount of packets registered by default*/
    public static final int INTERNAL_PACKETS = 12;

    public static boolean connected = false;

    // For the client-info packet system:
    private static int packetId = INTERNAL_PACKETS;

    public static int nextPacketID() {
        return packetId++;
    }

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

        // Server side
//        channel.registerMessage(startIndex++, PacketServerCommandTyped.class, PacketServerCommandTyped::toBytes, PacketServerCommandTyped::new, PacketServerCommandTyped::handle);
//        channel.registerMessage(startIndex++, PacketSendServerCommand.class, PacketSendServerCommand::toBytes, PacketSendServerCommand::new, PacketSendServerCommand::handle);
//        channel.registerMessage(startIndex++, PacketRequestDataFromServer.class, PacketRequestDataFromServer::toBytes, PacketRequestDataFromServer::new, PacketRequestDataFromServer::handle);
//        channel.registerMessage(startIndex++, PacketDumpItemInfo.class, PacketDumpItemInfo::toBytes, PacketDumpItemInfo::new, PacketDumpItemInfo::handle);
//        channel.registerMessage(startIndex++, PacketDumpBlockInfo.class, PacketDumpBlockInfo::toBytes, PacketDumpBlockInfo::new, PacketDumpBlockInfo::handle);
        channel.registerMessage(startIndex++, PacketSendPreferencesToClient.class, PacketSendPreferencesToClient::toBytes, PacketSendPreferencesToClient::new, PacketSendPreferencesToClient::handle);
        channel.registerMessage(startIndex++, PacketSetGuiStyle.class, PacketSetGuiStyle::toBytes, PacketSetGuiStyle::new, PacketSetGuiStyle::handle);

        // Client side
//        channel.registerMessage(startIndex++, PacketSendClientCommand.class, PacketSendClientCommand::toBytes, PacketSendClientCommand::new, PacketSendClientCommand::handle);
//        channel.registerMessage(startIndex++, PacketDataFromServer.class, PacketDataFromServer::toBytes, PacketDataFromServer::new, PacketDataFromServer::handle);
//        channel.registerMessage(startIndex++, PacketSendGuiData.class, PacketSendGuiData::toBytes, PacketSendGuiData::new, PacketSendGuiData::handle);
//        channel.registerMessage(startIndex++, PacketFinalizeLogin.class, PacketFinalizeLogin::toBytes, PacketFinalizeLogin::new, PacketFinalizeLogin::handle);
    }

    public static void registerStandardMessages(SimpleChannel channel) {

        // Server side
        channel.registerMessage(nextPacketID(), PacketServerCommandTyped.class, PacketServerCommandTyped::toBytes, PacketServerCommandTyped::new, PacketServerCommandTyped::handle);
        channel.registerMessage(nextPacketID(), PacketSendServerCommand.class, PacketSendServerCommand::toBytes, PacketSendServerCommand::new, PacketSendServerCommand::handle);
        channel.registerMessage(nextPacketID(), PacketDumpItemInfo.class, PacketDumpItemInfo::toBytes, PacketDumpItemInfo::new, PacketDumpItemInfo::handle);
        channel.registerMessage(nextPacketID(), PacketDumpBlockInfo.class, PacketDumpBlockInfo::toBytes, PacketDumpBlockInfo::new, PacketDumpBlockInfo::handle);

        // Client side
        channel.registerMessage(nextPacketID(), PacketSendClientCommand.class, PacketSendClientCommand::toBytes, PacketSendClientCommand::new, PacketSendClientCommand::handle);
        channel.registerMessage(nextPacketID(), PacketDataFromServer.class, PacketDataFromServer::toBytes, PacketDataFromServer::new, PacketDataFromServer::handle);
        channel.registerMessage(nextPacketID(), PacketFinalizeLogin.class, PacketFinalizeLogin::toBytes, PacketFinalizeLogin::new, PacketFinalizeLogin::handle);
    }

    // From client side only: send server command
    public static void sendCommand(SimpleChannel network, String modid, String command, @Nonnull TypedMap arguments) {
        network.sendToServer(new PacketSendServerCommand(modid, command, arguments));
    }
}
