package mcjty.lib.tileentity;

import mcjty.lib.bindings.IValue;
import mcjty.lib.blockcommands.ICommand;
import mcjty.lib.blockcommands.ICommandWithList;
import mcjty.lib.blockcommands.ICommandWithListResult;
import mcjty.lib.blockcommands.ICommandWithResult;

import java.util.HashMap;
import java.util.Map;

class AnnotationHolder {
    final Map<String, ICommand<?>> serverCommands = new HashMap<>();
    final Map<String, ICommandWithResult<?>> serverCommandsWithResult = new HashMap<>();
    final Map<String, ICommand<?>> clientCommands = new HashMap<>();
    final Map<String, ICommandWithListResult<?, ?>> serverCommandsWithListResult = new HashMap<>();
    final Map<String, ICommandWithList<?, ?>> clientCommandsWithList = new HashMap<>();
    final Map<String, IValue<?>> valueMap = new HashMap<>();
}
