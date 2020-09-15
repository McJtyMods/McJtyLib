package mcjty.lib.api.container;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

/**
 * This represents generic data that a container can sync from server to client automatically
 */
public interface IContainerDataListener {

    /// A unique ID for this data
    ResourceLocation getId();

    /// Return if the data is dirty and clear the data flag
    boolean isDirtyAndClear();

    /// Write data to buffer
    void toBytes(PacketBuffer buf);

    /// Read data from buffer
    void readBuf(PacketBuffer buf);
}
