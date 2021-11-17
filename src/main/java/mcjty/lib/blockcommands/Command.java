package mcjty.lib.blockcommands;

import mcjty.lib.tileentity.GenericTileEntity;

public class Command<T extends GenericTileEntity> {

    private final String name;
    private final ICommand<T> cmd;
    private final ICommandWithResult<T> cmdWithResult;
    private final ICommand<T> clientCommand;

    private Command(String name, ICommand<T> cmd, ICommandWithResult<T> cmdWithResult, ICommand<T> clientCommand) {
        this.name = name;
        this.cmd = cmd;
        this.clientCommand = clientCommand;
        this.cmdWithResult = cmdWithResult;
    }

    public String getName() {
        return name;
    }

    public ICommand<T> getCmd() {
        return cmd;
    }

    public ICommandWithResult<T> getCmdWithResult() {
        return cmdWithResult;
    }

    public ICommand<T> getClientCommand() {
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
