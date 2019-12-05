package mcjty.lib.network;

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

        // Server side
//        channel.registerMessage(startIndex++, PacketServerCommandTyped.class, PacketServerCommandTyped::toBytes, PacketServerCommandTyped::new, PacketServerCommandTyped::handle);
//        channel.registerMessage(startIndex++, PacketSendServerCommand.class, PacketSendServerCommand::toBytes, PacketSendServerCommand::new, PacketSendServerCommand::handle);
//        channel.registerMessage(startIndex++, PacketRequestDataFromServer.class, PacketRequestDataFromServer::toBytes, PacketRequestDataFromServer::new, PacketRequestDataFromServer::handle);
//        channel.registerMessage(startIndex++, PacketDumpItemInfo.class, PacketDumpItemInfo::toBytes, PacketDumpItemInfo::new, PacketDumpItemInfo::handle);
//        channel.registerMessage(startIndex++, PacketDumpBlockInfo.class, PacketDumpBlockInfo::toBytes, PacketDumpBlockInfo::new, PacketDumpBlockInfo::handle);
        int index1 = startIndex++;
        channel.registerMessage(index1, PacketSendPreferencesToClient.class, PacketSendPreferencesToClient::toBytes, PacketSendPreferencesToClient::new, PacketSendPreferencesToClient::handle);
        int index = startIndex++;
        channel.registerMessage(index, PacketSetGuiStyle.class, PacketSetGuiStyle::toBytes, PacketSetGuiStyle::new, PacketSetGuiStyle::handle);

        // Client side
//        debugRegister(channel, );(startIndex++, PacketSendClientCommand.class, PacketSendClientCommand::toBytes, PacketSendClientCommand::new, PacketSendClientCommand::handle);
//        debugRegister(channel, );(startIndex++, PacketDataFromServer.class, PacketDataFromServer::toBytes, PacketDataFromServer::new, PacketDataFromServer::handle);
//        debugRegister(channel, );(startIndex++, PacketSendGuiData.class, PacketSendGuiData::toBytes, PacketSendGuiData::new, PacketSendGuiData::handle);
//        debugRegister(channel, );(startIndex++, PacketFinalizeLogin.class, PacketFinalizeLogin::toBytes, PacketFinalizeLogin::new, PacketFinalizeLogin::handle);
    }

    public static void registerStandardMessages(int id, SimpleChannel channel) {

        // Server side
        int index6 = id++;
        channel.registerMessage(index6, PacketServerCommandTyped.class, PacketServerCommandTyped::toBytes, PacketServerCommandTyped::new, PacketServerCommandTyped::handle);
        int index5 = id++;
        channel.registerMessage(index5, PacketSendServerCommand.class, PacketSendServerCommand::toBytes, PacketSendServerCommand::new, PacketSendServerCommand::handle);
        int index4 = id++;
        channel.registerMessage(index4, PacketDumpItemInfo.class, PacketDumpItemInfo::toBytes, PacketDumpItemInfo::new, PacketDumpItemInfo::handle);
        int index3 = id++;
        channel.registerMessage(index3, PacketDumpBlockInfo.class, PacketDumpBlockInfo::toBytes, PacketDumpBlockInfo::new, PacketDumpBlockInfo::handle);

        // Client side
        int index2 = id++;
        channel.registerMessage(index2, PacketSendClientCommand.class, PacketSendClientCommand::toBytes, PacketSendClientCommand::new, PacketSendClientCommand::handle);
        int index1 = id++;
        channel.registerMessage(index1, PacketDataFromServer.class, PacketDataFromServer::toBytes, PacketDataFromServer::new, PacketDataFromServer::handle);
        int index = id++;
        channel.registerMessage(index, PacketFinalizeLogin.class, PacketFinalizeLogin::toBytes, PacketFinalizeLogin::new, PacketFinalizeLogin::handle);
    }

    // From client side only: send server command
    public static void sendCommand(SimpleChannel network, String modid, String command, @Nonnull TypedMap arguments) {
        network.sendToServer(new PacketSendServerCommand(modid, command, arguments));
    }

}
