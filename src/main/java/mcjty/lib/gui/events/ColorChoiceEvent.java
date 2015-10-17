package mcjty.lib.gui.events;

import mcjty.lib.gui.widgets.Widget;

public interface ColorChoiceEvent {
    void choiceChanged(Widget parent, Integer newColor);
}
