package mcjty.lib.gui.events;

import mcjty.lib.gui.icons.IIcon;
import mcjty.lib.gui.widgets.IconHolder;

public interface IconEvent {

    /// Return false if you don't want the icon to arrive here
    boolean iconArrives(IconHolder parent, IIcon icon);

    /// Return false if you don't want the icon to go away
    boolean iconLeaves(IconHolder parent, IIcon icon);

    /// Fires if the icon is clicked. Return false if you don't want any further processing
    boolean iconClicked(IconHolder parent, IIcon icon, int dx, int dy);
}
