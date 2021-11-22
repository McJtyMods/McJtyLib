package mcjty.lib.sync;

import java.lang.annotation.*;

/**
 * Annotate a (non-static) field with this annotation to declare that it requires syncing to the client (GUI) whenever
 * a container is open. To use this you need to call setupSync(this) on your DefaultContainerProvider. Note that this
 * syncing is one-way only (from server to client)
 *
 * Technical: DefaultContainerProvider.setupSync() will scan the GenericlTileEntity for fields with this annotation. If
 * the type is set in the annotation that will be used. Otherwise it will try to guess the type.
 * It will then setup an integer or data listener on the container which will detect whenever the field changes
 * server-side. When it changes a packet will be sent to all container listeners so that the clients get updated. This
 * will cause the client-side version of the field to get updated.
 * Side note: when the container first opens an update of all fields is forced to make sure the client-side tile entity
 * is correct. This packet can arrive late (after init of the screen) so the screen has to constantly update the
 * widgets (using bindings or manually)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface SyncToGui {

    /**
     * The type of this field. Default it will try to detect the type from the type of the field.
     * If you need to distinguish between short and int (for packet size) then specify it manually. The
     * default for integers is INT
     */
    SyncType type() default SyncType.AUTOMATIC;
}
