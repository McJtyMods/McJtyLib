package mcjty.lib.bindings;

import java.lang.annotation.*;

/**
 * Annotate a static Value (in your block entity) with this annotation support a client-sided synced value.
 * This can be used to sync the client side version of a value to the gui (through binding). In addition the
 * client side value will also be synced to the server (through the COMMAND_SYNC_BINDING command)
 *
 * Technical: the first time a GenericTileEntity is made of a given type the AnnotationHolder will be created. This
 * will scan all static fields of the tile entity. These fields should be instances of Value. A Value has a key
 * (representing a type and a name) as well as a getter and a setter.
 *
 * Whenever a Window.bind() happens client-side it will try to find the value with that name for the given tile entity.
 * It will then initialize the widget with the current (client-side) value of that field and setup a listener
 * on the component. That listener will update the client-side value every time the widget is modified by the user.
 * At the same time the listener will execute the COMMAND_SYNC_BINDING to sync the value to the server
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface GuiValue {
}
