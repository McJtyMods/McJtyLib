package mcjty.lib.multiblock;

import net.minecraft.resources.Identifier;

public interface IMultiblockConnector {

    /**
     * Get an ID representing the type of this multiblock
     */
    Identifier getId();

    int getMultiblockId();

    /**
     * This function should do nothing if the id didn't change (test for that!)
     */
    void setMultiblockId(int id);
}
