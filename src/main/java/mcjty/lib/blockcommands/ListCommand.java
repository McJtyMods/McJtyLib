package mcjty.lib.blockcommands;

import mcjty.lib.tileentity.GenericTileEntity;

/**
 * A command that gets executed serverside, calculates a list of data and sends that back to the client.
 * Annotate with @ServerCommand to register. Also see McJtyLib.registerListCommandInfo()
 */
public class ListCommand<TE extends GenericTileEntity, T> implements ICommand {

    private final String name;
    private final IRunnableWithListResult<TE, T> cmd;
    private final IRunnableWithList<TE, T> clientCommand;

    private ListCommand(String name, IRunnableWithListResult<TE, T> cmd, IRunnableWithList<TE, T> clientCommand) {
        this.name = name;
        this.cmd = cmd;
        this.clientCommand = clientCommand;
    }

    @Override
    public String getName() {
        return name;
    }

    public IRunnableWithListResult<TE, T> getCmd() {
        return cmd;
    }

    public IRunnableWithList<TE, T> getClientCommand() {
        return clientCommand;
    }

    /// Create a command without a result
    public static <E extends GenericTileEntity, S> ListCommand<E, S> create(String name, IRunnableWithListResult<E, S> command, IRunnableWithList<E, S> clientCommand) {
        return new ListCommand<E, S>(name, command, clientCommand);
    }
}
