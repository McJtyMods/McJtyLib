package mcjty.lib.blockcommands;

import mcjty.lib.tileentity.GenericTileEntity;

public class Command<T extends GenericTileEntity> {

    private final String name;
    private final ICommand<T> cmd;
    private final ICommandWithResult<T> cmdWithResult;
    private final ICommand<T> clientCommand;

    private Command(Builder<T> builder) {
        this.name = builder.name;
        this.cmd = builder.cmd;
        this.clientCommand = builder.clientCommand;
        this.cmdWithResult = builder.cmdWithResult;
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

    public static <E extends GenericTileEntity> Builder<E> builder(String name) {
        return new Builder<E>(name);
    }

    /// Create a command without a result
    public static <E extends GenericTileEntity> Command<E> create(String name, ICommand<E> command) {
        return new Builder<E>(name)
                .command(command)
                .build();
    }

    /// Create a command with a TypedMap result
    public static <E extends GenericTileEntity> Command<E> createWR(String name, ICommandWithResult<E> command, ICommand<E> clientCommand) {
        return new Builder<E>(name)
                .command(command)
                .clientCommand(clientCommand)
                .build();
    }

    public static class Builder<T extends GenericTileEntity> {

        private final String name;
        private ICommand<T> cmd = null;
        private ICommandWithResult<T> cmdWithResult = null;
        private ICommand<T> clientCommand = null;

        public Builder(String name) {
            this.name = name;
        }

        public Builder<T> command(ICommand<T> command) {
            this.cmd = command;
            return this;
        }

        public Builder<T> command(ICommandWithResult<T> command) {
            this.cmdWithResult = command;
            return this;
        }

        public Builder<T> clientCommand(ICommand<T> clientCommand) {
            this.clientCommand = clientCommand;
            return this;
        }

        public Command<T> build() {
            return new Command<T>(this);
        }
    }
}
