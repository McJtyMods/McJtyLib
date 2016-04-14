package mcjty.lib.gui.events;

import mcjty.lib.gui.widgets.Widget;

public interface BlockRenderEvent {
    void select(Widget parent);

    void doubleClick(Widget parent);
}
