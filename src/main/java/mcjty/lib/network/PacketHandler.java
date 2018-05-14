package mcjty.lib.network;

import mcjty.lib.typed.TypedMap;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

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
        networkWrapper.registerMessage(PacketServerCommandTyped.Handler.class, PacketServerCommandTyped.class, startIndex++, Side.SERVER);
        networkWrapper.registerMessage(PacketSendServerCommand.Handler.class, PacketSendServerCommand.class, startIndex++, Side.SERVER);
        networkWrapper.registerMessage(PacketRequestDataFromServer.Handler.class, PacketRequestDataFromServer.class, startIndex++, Side.SERVER);
//        networkWrapper.registerMessage(PacketUpdateNBTItem.Handler.class, PacketUpdateNBTItem.class, startIndex++, Side.SERVER);
//        networkWrapper.registerMessage(PacketUpdateNBTItemInventory.Handler.class, PacketUpdateNBTItemInventory.class, startIndex++, Side.SERVER);
        networkWrapper.registerMessage(PacketDumpItemInfo.Handler.class, PacketDumpItemInfo.class, startIndex++, Side.SERVER);
        networkWrapper.registerMessage(PacketDumpBlockInfo.Handler.class, PacketDumpBlockInfo.class, startIndex++, Side.SERVER);

        // Client side
        networkWrapper.registerMessage(PacketSendClientCommandHandler.class, PacketSendClientCommand.class, startIndex++, Side.CLIENT);
        networkWrapper.registerMessage(PacketDataFromServer.Handler.class, PacketDataFromServer.class, startIndex++, Side.CLIENT);
        networkWrapper.registerMessage(PacketSendGuiData.Handler.class, PacketSendGuiData.class, startIndex++, Side.CLIENT);

        return startIndex;
    }

    // From client side only: send server command
    public static void sendCommand(SimpleNetworkWrapper network, String modid, String command, @Nonnull TypedMap arguments) {
        network.sendToServer(new PacketSendServerCommand(modid, command, arguments));
    }
}
