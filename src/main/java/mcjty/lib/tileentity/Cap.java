package mcjty.lib.tileentity;

import java.lang.annotation.*;

/**
 * Annotate a LazyOptional field with this annotation to automatically
 * register it for getCapabilities().
 * This annotation can also be used with the actual capability (not the
 * lazyoptional) in which case it will automatically generate a LazyOptional
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface Cap {

    CapType type();
}
