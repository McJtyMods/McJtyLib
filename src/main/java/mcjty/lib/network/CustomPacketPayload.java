package mcjty.lib.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

// For compatibility with NeoForge
public interface CustomPacketPayload {

    void write(FriendlyByteBuf buf);

    ResourceLocation id();
}
