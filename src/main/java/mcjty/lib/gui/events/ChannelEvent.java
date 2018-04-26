package mcjty.lib.gui.events;

import mcjty.lib.gui.widgets.Widget;

/**
 * Generic event on the main window channel
 */
public interface ChannelEvent {

    void fire(Widget source, String id);
}
