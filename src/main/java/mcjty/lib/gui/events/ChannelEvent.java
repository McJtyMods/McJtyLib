package mcjty.lib.gui.events;

import mcjty.lib.gui.widgets.Widget;
import mcjty.lib.typed.TypedMap;

import javax.annotation.Nonnull;

/**
 * Generic event on the main window channel
 */
public interface ChannelEvent {

    void fire(@Nonnull Widget<?> source, @Nonnull TypedMap params);
}
