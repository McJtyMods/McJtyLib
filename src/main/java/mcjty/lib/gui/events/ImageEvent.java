package mcjty.lib.gui.events;

import mcjty.lib.gui.widgets.Widget;

public interface ImageEvent {
    void imageClicked(Widget<?> parent, int u, int v, int color);
}
