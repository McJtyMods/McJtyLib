package mcjty.lib.blockcommands;

import java.lang.annotation.*;

/**
 * Annotate a static Command, ListCommand, or ResultCommand (in your block entity) with this annotation to support sending this command
 * from the client to the server
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface ServerCommand {
}
