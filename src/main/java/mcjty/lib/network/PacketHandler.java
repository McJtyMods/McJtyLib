package mcjty.lib.network;

import mcjty.lib.typed.TypedMap;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class PacketHandler {

    /* Make sure this number is higher than the amount of packets registered by default*/
    public static final int INTERNAL_PACKETS = 12;

    public static boolean connected = false;

    // For the client-info packet system:
    private static int packetId = INTERNAL_PACKETS;

    public static Map<String, SimpleChannel> modNetworking = new HashMap<>();

    public static int nextPacketID() {
        return packetId++;
    }

    public static SimpleChannel registerMessages(String modid, String channelName) {
        SimpleChannel network = new SimpleChannel(channelName) {
            @Override
            public void sendToServer(IMessage message) {
                if (message instanceof IClientServerDelayed && !canBeSent(message)) {
                    return;
                }
                super.sendToServer(message);
            }
        };
        registerMessages(network);
        modNetworking.put(modid, network);
        return network;
    }

    // Only use client-side!
    private static boolean canBeSent(IMessage message) {
        return connected;
    }

    // Only use client-side!
    public static void onDisconnect() {
        connected = false;
    }


    private static void registerMessages(SimpleChannel networkWrapper) {
        int startIndex = 0;
        SimpleChannel channel = new SimpleChannel(networkWrapper);

        // Server side
        channel.registerMessageServer(startIndex++, PacketServerCommandTyped.class, PacketServerCommandTyped::toBytes, PacketServerCommandTyped::new, PacketServerCommandTyped::handle);
        channel.registerMessageServer(startIndex++, PacketSendServerCommand.class, PacketSendServerCommand::toBytes, PacketSendServerCommand::new, PacketSendServerCommand::handle);
        channel.registerMessageServer(startIndex++, PacketRequestDataFromServer.class, PacketRequestDataFromServer::toBytes, PacketRequestDataFromServer::new, PacketRequestDataFromServer::handle);
        channel.registerMessageServer(startIndex++, PacketDumpItemInfo.class, PacketDumpItemInfo::toBytes, PacketDumpItemInfo::new, PacketDumpItemInfo::handle);
        channel.registerMessageServer(startIndex++, PacketDumpBlockInfo.class, PacketDumpBlockInfo::toBytes, PacketDumpBlockInfo::new, PacketDumpBlockInfo::handle);

        // Client side
        channel.registerMessageClient(startIndex++, PacketSendClientCommand.class, PacketSendClientCommand::toBytes, PacketSendClientCommand::new, PacketSendClientCommand::handle);
        channel.registerMessageClient(startIndex++, PacketDataFromServer.class, PacketDataFromServer::toBytes, PacketDataFromServer::new, PacketDataFromServer::handle);
        channel.registerMessageClient(startIndex++, PacketSendGuiData.class, PacketSendGuiData::toBytes, PacketSendGuiData::new, PacketSendGuiData::handle);
        channel.registerMessageClient(startIndex++, PacketFinalizeLogin.class, PacketFinalizeLogin::toBytes, PacketFinalizeLogin::new, PacketFinalizeLogin::handle);
    }

    // From client side only: send server command
    public static void sendCommand(SimpleChannel network, String modid, String command, @Nonnull TypedMap arguments) {
        network.sendToServer(new PacketSendServerCommand(modid, command, arguments));
    }
}
