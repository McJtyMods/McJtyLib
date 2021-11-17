package mcjty.lib.bindings;

import java.lang.annotation.*;

/**
 * Annotate a static Value (in your block entity) with this annotation support a client-sided synced value
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface Val {
}
