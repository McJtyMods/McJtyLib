package mcjty.lib.gui.events;

import mcjty.lib.gui.icons.IIcon;

public interface IconClickedEvent {

    /// Fires if the icon is clicked. Return false if you don't want any further processing
    boolean iconClicked(IIcon icon, int dx, int dy);
}
