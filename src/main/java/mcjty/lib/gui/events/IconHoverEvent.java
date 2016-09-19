package mcjty.lib.gui.events;

import mcjty.lib.gui.icons.IIcon;
import mcjty.lib.gui.widgets.IconHolder;

public interface IconHoverEvent {
    void hover(IconHolder parent, IIcon icon, int dx, int dy);
}
