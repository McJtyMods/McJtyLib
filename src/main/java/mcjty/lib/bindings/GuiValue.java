package mcjty.lib.bindings;

import java.lang.annotation.*;

/**
 * Annotate a static Value (in your block entity) with this annotation support a client-sided synced value.
 * This can be used to sync the client side version of a value to the gui (through binding). In addition the
 * client side value will also be synced to the server (through the COMMAND_SYNC_BINDING command)
 *
 * You can also use this annotation with a regular (not a Value) field. In this case a value will be created
 * with a generated getter and setter (the setter will set the value and call setChanged() on the server). Otherwise
 * it works the same. When you use this annotation with a regular field you can provide a name. If you don't,
 * the name of the field itself will be used.
 * Additionally note that this field should not be static.
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
 * Examples:
 *
 *    // A value with the name 'name' and type String with explicit getter and setter. The actual field is separate from the Value
 *    private String name;
 *    @GuiValue
 *    public static final Value<?, String> VALUE_NAME = Value.create("name", Type.STRING, ConnectorTileEntity::getConnectorName, ConnectorTileEntity::setConnectorName);
 *
 *    // An enum value (enum can be a regular enum) with the name 'rotate' and explicit getter and setter
 *    private RotateMode rotateMode = RotateMode.ROTATE_0;
 *    @GuiValue
 *    public static final Value<BuilderTileEntity, String> VALUE_ROTATE = Value.createEnum("rotate", RotateMode.values(), BuilderTileEntity::getRotate, BuilderTileEntity::setRotate);
 *
 *    // An enum value (enum must extend NamedEnum) with name 'rotate' and generated getter and setter
 *    @GuiValue
 *    private RotateMode rotate = RotateMode.ROTATE_0;
 *
 *    // A string value with name 'name' and generated getter and setter
 *    @GuiValue
 *    private String name;
 *
 *    // A string value with name 'name' and generated getter and setter. The actual field has a different name
 *    @GuiValue(name = "name")
 *    private String myName;
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface GuiValue {

    String name() default "";
}
