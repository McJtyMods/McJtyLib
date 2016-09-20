package mcjty.lib.gui.events;

import mcjty.lib.gui.icons.IIcon;
import mcjty.lib.gui.widgets.IconHolder;

public interface IconHolderClickedEvent {

    /// Fires if the holder is clicked (with or without icon)
    void holderClicked(IconHolder parent, IIcon icon, int dx, int dy);
}
