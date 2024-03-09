package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

public class Networking {

    private static IPayloadRegistrar registrar;

    public static void registerMessages() {
        registrar = registrar(McJtyLib.MODID)
                .versioned("1.0")
                .optional();

        registrar.play(PacketSendPreferencesToClient.ID, PacketSendPreferencesToClient::create, handler -> handler.server(PacketSendPreferencesToClient::handle));
        registrar.play(PacketSetGuiStyle.ID, PacketSetGuiStyle::create, handler -> handler.server(PacketSetGuiStyle::handle));
        registrar.play(PacketOpenManual.ID, PacketOpenManual::create, handler -> handler.server(PacketOpenManual::handle));
        registrar.play(PacketContainerDataToClient.ID, PacketContainerDataToClient::create, handler -> handler.server(PacketContainerDataToClient::handle));
        registrar.play(PacketSendResultToClient.ID, PacketSendResultToClient::create, handler -> handler.server(PacketSendResultToClient::handle));


        // Server side
        registrar.play(PacketGetListFromServer.ID, PacketGetListFromServer::create, handler -> handler.server(PacketGetListFromServer::handle));
        registrar.play(PacketServerCommandTyped.ID, PacketServerCommandTyped::create, handler -> handler.server(PacketServerCommandTyped::handle));
        registrar.play(PacketSendServerCommand.ID, PacketSendServerCommand::create, handler -> handler.server(PacketSendServerCommand::handle));
        registrar.play(PacketRequestDataFromServer.ID, PacketRequestDataFromServer::create, handler -> handler.server(PacketRequestDataFromServer::handle));

        // Client side
        registrar.play(PacketSendClientCommand.ID, PacketSendClientCommand::create, handler -> handler.client(PacketSendClientCommand::handle));
        registrar.play(PacketDataFromServer.ID, PacketDataFromServer::create, handler -> handler.client(PacketDataFromServer::handle));
        registrar.play(PacketFinalizeLogin.ID, PacketFinalizeLogin::create, handler -> handler.client(PacketFinalizeLogin::handle));
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