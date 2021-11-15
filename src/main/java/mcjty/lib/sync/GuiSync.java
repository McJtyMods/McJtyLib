package mcjty.lib.sync;

import java.lang.annotation.*;

/**
 * Annotate a field with this annotation to declare that it requires syncing to the client (GUI) whenever a container
 * is open. To use this you need to call setupSync(this) on your DefaultContainerProvider
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface GuiSync {

    /**
     * The type of this field. Default it will try to detect the type from the type of the field.
     * If you need to distinguish between short and int (for packet size) then specify it manually. The
     * default for integers is INT
     */
    SyncType type() default SyncType.AUTOMATIC;
}
