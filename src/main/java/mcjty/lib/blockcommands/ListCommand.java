package mcjty.lib.blockcommands;

import mcjty.lib.tileentity.GenericTileEntity;

public class ListCommand<TE extends GenericTileEntity, T> {

    private final String name;
    private final ICommandWithListResult<TE, T> cmd;
    private final ICommandWithList<TE, T> clientCommand;

    private ListCommand(String name, ICommandWithListResult<TE, T> cmd, ICommandWithList<TE, T> clientCommand) {
        this.name = name;
        this.cmd = cmd;
        this.clientCommand = clientCommand;
    }

    public String getName() {
        return name;
    }

    public ICommandWithListResult<TE, T> getCmd() {
        return cmd;
    }

    public ICommandWithList<TE, T> getClientCommand() {
        return clientCommand;
    }

    /// Create a command without a result
    public static <E extends GenericTileEntity, S> ListCommand<E, S> create(String name, ICommandWithListResult<E, S> command, ICommandWithList<E, S> clientCommand) {
        return new ListCommand<E, S>(name, command, clientCommand);
    }
}
