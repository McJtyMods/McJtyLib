package mcjty.lib.bindings;

import java.lang.annotation.*;

/**
 * Annotate a static Value (in your block entity) with this annotation support a client-sided synced value.
 * This can be used to sync the client side version of a value to the gui (through binding). In addition the
 * client side value will also be synced to the server (through the COMMAND_SYNC_BINDING command)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface GuiValue {
}
