package mcjty.lib.multiblock;

import net.minecraft.resources.ResourceLocation;

public interface IMultiblockConnector {

    /**
     * Get an ID representing the type of this multiblock
     */
    ResourceLocation getId();

    int getMultiblockId();

    /**
     * This function should do nothing if the id didn't change (test for that!)
     */
    void setMultiblockId(int id);
}
