package mcjty.lib.tileentity;

import mcjty.lib.blockcommands.ICommand;
import mcjty.lib.blockcommands.ICommandWithResult;

import java.util.HashMap;
import java.util.Map;

class CommandHolder {
    final Map<String, ICommand> serverCommands = new HashMap<>();
    final Map<String, ICommandWithResult> serverCommandsWithResult = new HashMap<>();
    final Map<String, ICommand> clientCommands = new HashMap<>();
}
