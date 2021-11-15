package mcjty.lib.tileentity;

import java.lang.annotation.*;

/**
 * Annotate a LazyOptional<IItemHandler> field with this annotation to automatically
 * register it for getCapabilities()
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface Cap {

    CapType type();
}
