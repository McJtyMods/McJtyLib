package mcjty.lib.syncpositional;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public interface IPositionalData {

    /// Unique Id for this type of positional data
    ResourceLocation getId();

    /**
     * Convert this data to a packet
     */
    void toBytes(FriendlyByteBuf buf);
}
