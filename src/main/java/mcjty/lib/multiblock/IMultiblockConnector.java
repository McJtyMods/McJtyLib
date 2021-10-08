package mcjty.lib.multiblock;

public interface IMultiblockConnector {

    int getMultiblockId();

    /**
     * This function should do nothing if the id didn't change (test for that!)
     */
    void setMultiblockId(int id);
}
