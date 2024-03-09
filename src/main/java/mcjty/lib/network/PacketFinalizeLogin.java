package mcjty.lib.network;

import mcjty.lib.McJtyLib;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

/**
 * This is sent from the server to the client after the login has occured so that packets that implement
 * IClientServerDelayed can be sent
 */
public record PacketFinalizeLogin() implements CustomPacketPayload {

    public static final ResourceLocation ID = new ResourceLocation(McJtyLib.MODID, "finalize_login");

    @Override
    public void write(FriendlyByteBuf buf) {
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static PacketFinalizeLogin create(FriendlyByteBuf buf) {
        return new PacketFinalizeLogin();
    }

    public void handle(PlayPayloadContext ctx) {
        finalizeClientLogin();
    }

    private void finalizeClientLogin() {
    }

}