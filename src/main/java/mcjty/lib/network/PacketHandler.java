package mcjty.lib.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static SimpleNetworkWrapper registerMessages(String channelName) {
        SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);

        // Server side
        network.registerMessage(PacketSetGuiStyle.class, PacketSetGuiStyle.class, nextID(), Side.SERVER);
        network.registerMessage(PacketServerCommand.class, PacketServerCommand.class, nextID(), Side.SERVER);
        network.registerMessage(PacketRequestIntegerFromServer.class, PacketRequestIntegerFromServer.class, nextID(), Side.SERVER);
        network.registerMessage(PacketUpdateNBTItem.class, PacketUpdateNBTItem.class, nextID(), Side.SERVER);

        // Client side
        network.registerMessage(PacketIntegerFromServer.class, PacketIntegerFromServer.class, nextID(), Side.CLIENT);

        return network;
    }
}
