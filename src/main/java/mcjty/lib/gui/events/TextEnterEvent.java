package mcjty.lib.gui.events;

import mcjty.lib.gui.widgets.Widget;

public interface TextEnterEvent {
    void textEntered(Widget<?> parent, String newText);
}
