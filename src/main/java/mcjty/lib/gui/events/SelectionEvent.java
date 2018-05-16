package mcjty.lib.gui.events;

import mcjty.lib.gui.widgets.Widget;

public interface SelectionEvent {
    void select(Widget<?> parent, int index);

    void doubleClick(Widget<?> parent, int index);
}
