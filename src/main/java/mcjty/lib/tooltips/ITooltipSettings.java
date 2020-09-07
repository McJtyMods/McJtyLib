package mcjty.lib.tooltips;

import mcjty.lib.gui.ManualEntry;

/**
 * Implement this interface in an item or block to control the maximum width of the tooltip
 */
public interface ITooltipSettings {

    default int getMaxWidth() { return 200; }

    default ManualEntry getManualEntry() { return ManualEntry.EMPTY; }
}
