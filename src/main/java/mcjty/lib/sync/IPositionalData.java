package mcjty.lib.sync;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public interface IPositionalData {

    /// Unique Id for this type of positional data
    ResourceLocation getId();

    /**
     * Convert this data to a packet
     */
    void toBytes(PacketBuffer buf);
}
