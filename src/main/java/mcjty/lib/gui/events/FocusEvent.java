package mcjty.lib.gui.events;

import mcjty.lib.gui.Window;
import mcjty.lib.gui.widgets.Widget;

public interface FocusEvent {
    void focus(Window parent, Widget<?> focused);
}
