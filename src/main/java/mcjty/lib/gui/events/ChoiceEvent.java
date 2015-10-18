package mcjty.lib.gui.events;

import mcjty.lib.gui.widgets.Widget;

public interface ChoiceEvent {
    void choiceChanged(Widget parent, String newChoice);
}
