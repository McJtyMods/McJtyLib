package mcjty.lib.network;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@Environment(EnvType.CLIENT)
public final class ClientNetworking {

    public static void registerReceivers() {

        ClientPlayNetworking.registerGlobalReceiver(PacketAttachmentData.TYPE,(packet,context)->packet.handle(new PayloadContext(context.player(),context.client())));

        ClientPlayNetworking.registerGlobalReceiver(PacketSendPreferencesToClient.TYPE,(packet,context)->packet.handle(new PayloadContext(context.player(),context.client())));

        ClientPlayNetworking.registerGlobalReceiver(PacketContainerDataToClient.TYPE,(packet,context)->packet.handle(new PayloadContext(context.player(),context.client())));

        ClientPlayNetworking.registerGlobalReceiver(PacketSendResultToClient.TYPE,(packet,context)->packet.handle(new PayloadContext(context.player(),context.client())));

        ClientPlayNetworking.registerGlobalReceiver(PacketSendClientCommand.TYPE,(packet,context)->packet.handle(new PayloadContext(context.player(),context.client())));

        ClientPlayNetworking.registerGlobalReceiver(PacketDataFromServer.TYPE,(packet,context)->packet.handle(new PayloadContext(context.player(),context.client())));

        ClientPlayNetworking.registerGlobalReceiver(PacketFinalizeLogin.TYPE,(packet,context)->packet.handle(new PayloadContext(context.player(),context.client())));

    }

    public static void sendToServer(CustomPacketPayload packet) {
        if(ClientPlayNetworking.canSend(packet.type()))ClientPlayNetworking.send(packet);
    }

}
