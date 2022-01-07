package mcjty.lib.blockcommands;

import mcjty.lib.tileentity.GenericTileEntity;

/**
 * A command that gets executed serverside, calculates a list of data and sends that back to the client.
 * Annotate with @ServerCommand to register. Also see McJtyLib.registerListCommandInfo()
 */
public record ListCommand<TE extends GenericTileEntity, T>(String name,
                                                           IRunnableWithListResult<TE, T> cmd,
                                                           IRunnableWithList<TE, T> clientCommand) implements ICommand {

    /// Create a command without a result
    public static <E extends GenericTileEntity, S> ListCommand<E, S> create(String name, IRunnableWithListResult<E, S> command, IRunnableWithList<E, S> clientCommand) {
        return new ListCommand<E, S>(name, command, clientCommand);
    }
}
