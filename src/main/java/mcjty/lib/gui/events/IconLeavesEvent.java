package mcjty.lib.gui.events;

import mcjty.lib.gui.icons.IIcon;

public interface IconLeavesEvent {

    /// Return false if you don't want the icon to go away
    boolean iconLeaves(IIcon icon);
}
