package mcjty.lib.bindings;

import java.lang.annotation.*;

/**
 * Annotate a static Value (in your block entity) with this annotation support a client-sided synced value.
 * This can be used to sync the client side version of a value to the gui (through binding)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface GuiValue {
}
