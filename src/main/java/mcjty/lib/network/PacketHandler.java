package mcjty.lib.network;

import mcjty.lib.network.clientinfo.InfoPacketClient;
import mcjty.lib.network.clientinfo.InfoPacketServer;
import mcjty.lib.network.clientinfo.PacketGetInfoFromServer;
import mcjty.lib.network.clientinfo.PacketReturnInfoToClient;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.Map;

public class PacketHandler {

    /* Make sure this number is higher than the amount of packets registered by default*/
    public static final int INTERNAL_PACKETS = 12;

    // For the client-info packet system:
    private static int packetId = INTERNAL_PACKETS;

    private static Map<Integer,Class<? extends InfoPacketClient>> clientInfoPackets = new HashMap<>();
    private static Map<Integer,Class<? extends InfoPacketServer>> serverInfoPackets = new HashMap<>();
    private static Map<Class<? extends InfoPacketClient>,Integer> clientInfoPacketsToId = new HashMap<>();
    private static Map<Class<? extends InfoPacketServer>,Integer> serverInfoPacketsToId = new HashMap<>();

    public static Map<String,SimpleNetworkWrapper> modNetworking = new HashMap<>();

    public static int nextPacketID() {
        return packetId++;
    }

    public static void register(Integer id, Class<? extends InfoPacketServer> serverClass, Class<? extends InfoPacketClient> clientClass) {
        serverInfoPackets.put(id, serverClass);
        clientInfoPackets.put(id, clientClass);
        serverInfoPacketsToId.put(serverClass, id);
        clientInfoPacketsToId.put(clientClass, id);
    }

    public static Class<? extends InfoPacketServer> getServerInfoPacket(int id) {
        return serverInfoPackets.get(id);
    }

    public static Integer getServerInfoPacketId(Class<? extends InfoPacketServer> clazz) {
        return serverInfoPacketsToId.get(clazz);
    }

    public static Class<? extends InfoPacketClient> getClientInfoPacket(int id) {
        return clientInfoPackets.get(id);
    }

    public static Integer getClientInfoPacketId(Class<? extends InfoPacketClient> clazz) {
        return clientInfoPacketsToId.get(clazz);
    }

    @Deprecated
    public static int nextID() {
        return nextPacketID();
    }

    public static SimpleNetworkWrapper registerMessages(String modid, String channelName) {
        SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
        registerMessages(network);
        modNetworking.put(modid, network);
        return network;
    }

    public static int registerMessages(SimpleNetworkWrapper networkWrapper){
        return registerMessages(networkWrapper, 0);
    }

    public static int registerMessages(SimpleNetworkWrapper networkWrapper, int startIndex){

        // Server side
       // networkWrapper.registerMessage(PacketSetGuiStyle.Handler.class, PacketSetGuiStyle.class, startIndex++, Side.SERVER);
        networkWrapper.registerMessage(PacketServerCommand.Handler.class, PacketServerCommand.class, startIndex++, Side.SERVER);
        networkWrapper.registerMessage(PacketRequestIntegerFromServer.Handler.class, PacketRequestIntegerFromServer.class, startIndex++, Side.SERVER);
//        networkWrapper.registerMessage(PacketUpdateNBTItem.Handler.class, PacketUpdateNBTItem.class, startIndex++, Side.SERVER);
//        networkWrapper.registerMessage(PacketUpdateNBTItemInventory.Handler.class, PacketUpdateNBTItemInventory.class, startIndex++, Side.SERVER);
        networkWrapper.registerMessage(PacketGetInfoFromServer.Handler.class, PacketGetInfoFromServer.class, nextID(), Side.SERVER);
        networkWrapper.registerMessage(PacketDumpItemInfo.Handler.class, PacketDumpItemInfo.class, startIndex++, Side.SERVER);
        networkWrapper.registerMessage(PacketDumpBlockInfo.Handler.class, PacketDumpBlockInfo.class, startIndex++, Side.SERVER);

        // Client side
        networkWrapper.registerMessage(PacketIntegerFromServer.Handler.class, PacketIntegerFromServer.class, startIndex++, Side.CLIENT);
        networkWrapper.registerMessage(PacketReturnInfoToClient.Handler.class, PacketReturnInfoToClient.class, nextID(), Side.CLIENT);
        networkWrapper.registerMessage(PacketSendGuiData.Handler.class, PacketSendGuiData.class, startIndex++, Side.CLIENT);

        return startIndex;
    }
}
