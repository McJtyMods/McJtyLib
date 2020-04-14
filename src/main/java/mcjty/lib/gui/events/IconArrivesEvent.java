package mcjty.lib.gui.events;

import mcjty.lib.gui.icons.IIcon;

public interface IconArrivesEvent {

    /// Return false if you don't want the icon to arrive here
    boolean iconArrives(IIcon icon);

}
