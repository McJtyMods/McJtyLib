package mcjty.lib.gui.events;

import mcjty.lib.gui.widgets.Widget;

public interface ValueEvent {
    void valueChanged(Widget<?> parent, int newValue);
}
