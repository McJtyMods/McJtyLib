package mcjty.lib.api.machineinfo;

/**
 * Implement this capability based interface in your TE if you want the 'Machine Information Module'
 * from RFTools to be able to display information from your block.
 */
public interface IMachineInformation {

    /// Get the amount of tags that this machine supports.
    int getTagCount();

    /// Get the name of a specific tag.
    String getTagName(int index);

    /// Get the description for a specific tag.
    String getTagDescription(int index);

    /**
     * Get specific information for the given tag.
     * This method is called roughly once every second on the server side.
     */
    String getData(int index, long millis);
}
