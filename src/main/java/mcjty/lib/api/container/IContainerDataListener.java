package mcjty.lib.api.container;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;

/**
 * This represents generic data that a container can sync from server to client automatically
 */
public interface IContainerDataListener {

    /// A unique ID for this data
    Identifier getId();

    /// Return if the data is dirty and clear the data flag
    boolean isDirtyAndClear();

    /// Write data to buffer
    void toBytes(RegistryFriendlyByteBuf buf);

    /// Read data from buffer
    void readBuf(RegistryFriendlyByteBuf buf);
}
