package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.NetworkDirection;
import net.neoforged.neoforge.network.simple.SimpleChannel;

public class Networking {

    private static IPayloadRegistrar registrar;

    public static void registerMessages() {
        registrar = registrar(McJtyLib.MODID)
                .versioned("1.0")
                .optional();

        registrar.play(PacketSendPreferencesToClient.class, PacketSendPreferencesToClient::create, handler -> handler.server(PacketSendPreferencesToClient::handle));
        registrar.play(PacketSetGuiStyle.class, PacketSetGuiStyle::create, handler -> handler.server(PacketSetGuiStyle::handle));
        registrar.play(PacketOpenManual.class, PacketOpenManual::create, handler -> handler.server(PacketOpenManual::handle));
        registrar.play(PacketContainerDataToClient.class, PacketContainerDataToClient::create, handler -> handler.server(PacketContainerDataToClient::handle));
        registrar.play(PacketSendResultToClient.class, PacketSendResultToClient::create, handler -> handler.server(PacketSendResultToClient::handle));


        // Server side
        registrar.play(PacketGetListFromServer.class, PacketGetListFromServer::create, handler -> handler.server(PacketGetListFromServer::handle));
        registrar.play(PacketServerCommandTyped.class, PacketServerCommandTyped::create, handler -> handler.server(PacketServerCommandTyped::handle));
        registrar.play(PacketSendServerCommand.class, PacketSendServerCommand::create, handler -> handler.server(PacketSendServerCommand::handle));
        registrar.play(PacketRequestDataFromServer.class, PacketRequestDataFromServer::create, handler -> handler.server(PacketRequestDataFromServer::handle));

        // Client side
        registrar.play(PacketSendClientCommand.class, PacketSendClientCommand::create, handler -> handler.client(PacketSendClientCommand::handle));
        registrar.play(PacketDataFromServer.class, PacketDataFromServer::create, handler -> handler.client(PacketDataFromServer::handle));
        registrar.play(PacketFinalizeLogin.class, PacketFinalizeLogin::create, handler -> handler.client(PacketFinalizeLogin::handle));
    }

    public static void sendToServer(Object msg) {
        registrar.getChannel().sendToServer(msg);
    }

    public static SimpleChannel getChannel() {
        return registrar.getChannel();
    }

    public static <T> void sendToPlayer(T packet, Player player) {
        registrar.getChannel().sendTo(packet, ((ServerPlayer)player).connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static IPayloadRegistrar registrar(String modid) {
        return new SimpleWrapperRegistrar(modid);
    }
}