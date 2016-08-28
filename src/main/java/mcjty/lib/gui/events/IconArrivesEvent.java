package mcjty.lib.gui.events;

import mcjty.lib.gui.icons.IIcon;
import mcjty.lib.gui.widgets.IconHolder;

public interface IconArrivesEvent {

    /// Return false if you don't want the icon to arrive here
    boolean iconArrives(IconHolder parent, IIcon icon);

}
