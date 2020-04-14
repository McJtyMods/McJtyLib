package mcjty.lib.gui.events;

import mcjty.lib.gui.icons.IIcon;

public interface IconHolderClickedEvent {

    /// Fires if the holder is clicked (with or without icon)
    void holderClicked(IIcon icon, int dx, int dy);
}
