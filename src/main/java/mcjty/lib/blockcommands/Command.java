package mcjty.lib.blockcommands;

import mcjty.lib.tileentity.GenericTileEntity;

public class Command<T extends GenericTileEntity> {

    private final String name;
    private final ICommand<T> cmd;

    private Command(String name, ICommand<T> cmd) {
        this.name = name;
        this.cmd = cmd;
    }

    public String getName() {
        return name;
    }

    public ICommand<T> getCmd() {
        return cmd;
    }

    public static <E extends GenericTileEntity> Builder<E> create(String name) {
        return new Builder<E>(name);
    }

    public static class Builder<T extends GenericTileEntity> {

        private final String name;
        private ICommand<T> cmd = (te, player, params) -> {};

        public Builder(String name) {
            this.name = name;
        }

        public Builder<T> command(ICommand<T> command) {
            this.cmd = command;
            return this;
        }

        public Command<T> buildCommand(ICommand<T> command) {
            this.cmd = command;
            return build();
        }

        public Command<T> build() {
            return new Command<T>(name, cmd);
        }
    }
}
