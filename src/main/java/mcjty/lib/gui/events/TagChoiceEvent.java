package mcjty.lib.gui.events;

import mcjty.lib.gui.widgets.Widget;

public interface TagChoiceEvent {
    void tagChanged(Widget<?> parent, String newTag);
}
