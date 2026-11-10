package mcjty.lib.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public final class Networking {

    public static void registerMessages() {

        PayloadTypeRegistry.playC2S().register(PacketAttachmentData.TYPE,PacketAttachmentData.CODEC);

        PayloadTypeRegistry.playC2S().register(PacketSetGuiStyle.TYPE,PacketSetGuiStyle.CODEC);

        PayloadTypeRegistry.playC2S().register(PacketOpenManual.TYPE,PacketOpenManual.CODEC);

        PayloadTypeRegistry.playC2S().register(PacketGetListFromServer.TYPE,PacketGetListFromServer.CODEC);

        PayloadTypeRegistry.playC2S().register(PacketServerCommandTyped.TYPE,PacketServerCommandTyped.CODEC);

        PayloadTypeRegistry.playC2S().register(PacketSendServerCommand.TYPE,PacketSendServerCommand.CODEC);

        PayloadTypeRegistry.playC2S().register(PacketRequestDataFromServer.TYPE,PacketRequestDataFromServer.CODEC);

        PayloadTypeRegistry.playS2C().register(PacketAttachmentData.TYPE,PacketAttachmentData.CODEC);

        PayloadTypeRegistry.playS2C().register(PacketSendPreferencesToClient.TYPE,PacketSendPreferencesToClient.CODEC);

        PayloadTypeRegistry.playS2C().register(PacketContainerDataToClient.TYPE,PacketContainerDataToClient.CODEC);

        PayloadTypeRegistry.playS2C().register(PacketSendResultToClient.TYPE,PacketSendResultToClient.CODEC);

        PayloadTypeRegistry.playS2C().register(PacketSendClientCommand.TYPE,PacketSendClientCommand.CODEC);

        PayloadTypeRegistry.playS2C().register(PacketDataFromServer.TYPE,PacketDataFromServer.CODEC);

        PayloadTypeRegistry.playS2C().register(PacketFinalizeLogin.TYPE,PacketFinalizeLogin.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(PacketAttachmentData.TYPE,(packet,context)->packet.handle(new PayloadContext(context.player(),context.server())));

        ServerPlayNetworking.registerGlobalReceiver(PacketSetGuiStyle.TYPE,(packet,context)->packet.handle(new PayloadContext(context.player(),context.server())));

        ServerPlayNetworking.registerGlobalReceiver(PacketOpenManual.TYPE,(packet,context)->packet.handle(new PayloadContext(context.player(),context.server())));

        ServerPlayNetworking.registerGlobalReceiver(PacketGetListFromServer.TYPE,(packet,context)->packet.handle(new PayloadContext(context.player(),context.server())));

        ServerPlayNetworking.registerGlobalReceiver(PacketServerCommandTyped.TYPE,(packet,context)->packet.handle(new PayloadContext(context.player(),context.server())));

        ServerPlayNetworking.registerGlobalReceiver(PacketSendServerCommand.TYPE,(packet,context)->packet.handle(new PayloadContext(context.player(),context.server())));

        ServerPlayNetworking.registerGlobalReceiver(PacketRequestDataFromServer.TYPE,(packet,context)->packet.handle(new PayloadContext(context.player(),context.server())));

    }

    public static void sendToServer(CustomPacketPayload packet) {
        ClientNetworking.sendToServer(packet);
    }

    public static <T extends CustomPacketPayload> void sendToPlayer(T packet,Player player) {
        if(ServerPlayNetworking.canSend((ServerPlayer)player,packet.type()))ServerPlayNetworking.send((ServerPlayer)player,packet);
    }

}
