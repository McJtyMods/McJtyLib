package mcjty.lib.blockcommands;

import mcjty.lib.tileentity.GenericTileEntity;

public class Command<TE extends GenericTileEntity> {

    private final String name;
    private final ICommand<TE> cmd;
    private final ICommandWithResult<TE> cmdWithResult;
    private final ICommand<TE> clientCommand;

    private Command(String name, ICommand<TE> cmd, ICommandWithResult<TE> cmdWithResult, ICommand<TE> clientCommand) {
        this.name = name;
        this.cmd = cmd;
        this.clientCommand = clientCommand;
        this.cmdWithResult = cmdWithResult;
    }

    public String getName() {
        return name;
    }

    public ICommand<TE> getCmd() {
        return cmd;
    }

    public ICommandWithResult<TE> getCmdWithResult() {
        return cmdWithResult;
    }

    public ICommand<TE> getClientCommand() {
        return clientCommand;
    }

    /// Create a command without a result
    public static <E extends GenericTileEntity> Command<E> create(String name, ICommand<E> command) {
        return new Command<>(name, command, null, null);
    }

    /// Create a command with a TypedMap result
    public static <E extends GenericTileEntity> Command<E> createWR(String name, ICommandWithResult<E> command, ICommand<E> clientCommand) {
        return new Command<>(name, null, command, clientCommand);
    }
}
