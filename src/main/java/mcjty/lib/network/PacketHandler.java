package mcjty.lib.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {

    /* Make sure this number is higher than the amount of packets registered by default*/
    private static int ID = 12;

    public static int nextID() {
        return ID++;
    }

    public static SimpleNetworkWrapper registerMessages(String channelName) {
        SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
        registerMessages(network);
        return network;
    }

    public static int registerMessages(SimpleNetworkWrapper networkWrapper){
        return registerMessages(networkWrapper, 0);
    }

    public static int registerMessages(SimpleNetworkWrapper networkWrapper, int startIndex){

        // Server side
        networkWrapper.registerMessage(PacketSetGuiStyle.class, PacketSetGuiStyle.class, startIndex++, Side.SERVER);
        networkWrapper.registerMessage(PacketServerCommand.class, PacketServerCommand.class, startIndex++, Side.SERVER);
        networkWrapper.registerMessage(PacketRequestIntegerFromServer.class, PacketRequestIntegerFromServer.class, startIndex++, Side.SERVER);
        networkWrapper.registerMessage(PacketUpdateNBTItem.class, PacketUpdateNBTItem.class, startIndex++, Side.SERVER);

        // Client side
        networkWrapper.registerMessage(PacketIntegerFromServer.class, PacketIntegerFromServer.class, startIndex++, Side.CLIENT);
        networkWrapper.registerMessage(PacketSendPreferencesToClientHandler.class, PacketSendPreferencesToClient.class, startIndex++, Side.CLIENT);

        return startIndex;
    }
}
