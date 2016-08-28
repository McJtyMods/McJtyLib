package mcjty.lib.gui.events;

import mcjty.lib.gui.icons.IIcon;
import mcjty.lib.gui.widgets.IconHolder;

public interface IconClickedEvent {

    /// Fires if the icon is clicked. Return false if you don't want any further processing
    boolean iconClicked(IconHolder parent, IIcon icon, int dx, int dy);
}
