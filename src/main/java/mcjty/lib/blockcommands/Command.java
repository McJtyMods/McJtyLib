package mcjty.lib.blockcommands;

import mcjty.lib.tileentity.GenericTileEntity;

/**
 * A command that gets executed serverside. Annotate with @ServerCommand to register
 */
public class Command<TE extends GenericTileEntity> implements ICommand {

    private final String name;
    private final IRunnable<TE> cmd;

    private Command(String name, IRunnable<TE> cmd) {
        this.name = name;
        this.cmd = cmd;
    }

    @Override
    public String getName() {
        return name;
    }

    public IRunnable<TE> getCmd() {
        return cmd;
    }

    /// Create a command without a result
    public static <E extends GenericTileEntity> Command<E> create(String name, IRunnable<E> command) {
        return new Command<>(name, command);
    }
}
