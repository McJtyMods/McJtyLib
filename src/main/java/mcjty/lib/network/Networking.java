package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class Networking {

    public static void registerMessages(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(McJtyLib.MODID)
                .versioned("1.0")
                .optional();

        registrar.playToClient(PacketSendPreferencesToClient.TYPE, PacketSendPreferencesToClient.CODEC, PacketSendPreferencesToClient::handle);
        registrar.playToServer(PacketSetGuiStyle.TYPE, PacketSetGuiStyle.CODEC, PacketSetGuiStyle::handle);
        registrar.playToClient(PacketOpenManual.TYPE, PacketOpenManual.CODEC, PacketOpenManual::handle);
        registrar.playToClient(PacketContainerDataToClient.TYPE, PacketContainerDataToClient.CODEC, PacketContainerDataToClient::handle);
        registrar.playToClient(PacketSendResultToClient.TYPE, PacketSendResultToClient.CODEC, PacketSendResultToClient::handle);


        // Server side
        registrar.playToServer(PacketGetListFromServer.TYPE, PacketGetListFromServer.CODEC, PacketGetListFromServer::handle);
        registrar.playToServer(PacketServerCommandTyped.TYPE, PacketServerCommandTyped.CODEC, PacketServerCommandTyped::handle);
        registrar.playToServer(PacketSendServerCommand.TYPE, PacketSendServerCommand.CODEC, PacketSendServerCommand::handle);
        registrar.playToServer(PacketRequestDataFromServer.TYPE, PacketRequestDataFromServer.CODEC, PacketRequestDataFromServer::handle);

        // Client side
        registrar.playToClient(PacketSendClientCommand.TYPE, PacketSendClientCommand.CODEC, PacketSendClientCommand::handle);
        registrar.playToClient(PacketDataFromServer.TYPE, PacketDataFromServer.CODEC, PacketDataFromServer::handle);
        registrar.playToClient(PacketFinalizeLogin.TYPE, PacketFinalizeLogin.CODEC, PacketFinalizeLogin::handle);
    }

    public static void sendToServer(CustomPacketPayload msg) {
        PacketDistributor.SERVER.with(null).send(msg);
    }

    public static <T extends CustomPacketPayload> void sendToPlayer(T packet, Player player) {
        PacketDistributor.PLAYER.with((ServerPlayer) player).send(packet);
    }
}