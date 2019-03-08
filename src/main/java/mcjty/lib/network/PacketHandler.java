package mcjty.lib.network;

import mcjty.lib.McJtyLibClient;
import mcjty.lib.thirteen.SimpleChannel;
import mcjty.lib.typed.TypedMap;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class PacketHandler {

    /* Make sure this number is higher than the amount of packets registered by default*/
    public static final int INTERNAL_PACKETS = 12;

    // For the client-info packet system:
    private static int packetId = INTERNAL_PACKETS;

    public static Map<String,SimpleNetworkWrapper> modNetworking = new HashMap<>();

    public static int nextPacketID() {
        return packetId++;
    }

    public static SimpleNetworkWrapper registerMessages(String modid, String channelName) {
        SimpleNetworkWrapper network = new SimpleNetworkWrapper(channelName) {
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

    @SideOnly(Side.CLIENT)
    private static boolean canBeSent(IMessage message) {
        return McJtyLibClient.connected;
    }

    @SideOnly(Side.CLIENT)
    public static void onDisconnect() {
        McJtyLibClient.connected = false;
    }


    private static void registerMessages(SimpleNetworkWrapper networkWrapper) {
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
    public static void sendCommand(SimpleNetworkWrapper network, String modid, String command, @Nonnull TypedMap arguments) {
        network.sendToServer(new PacketSendServerCommand(modid, command, arguments));
    }
}
