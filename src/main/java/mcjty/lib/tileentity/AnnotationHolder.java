package mcjty.lib.tileentity;

import mcjty.lib.blockcommands.IRunnable;
import mcjty.lib.blockcommands.IRunnableWithList;
import mcjty.lib.blockcommands.IRunnableWithListResult;
import mcjty.lib.blockcommands.IRunnableWithResult;

import java.util.HashMap;
import java.util.Map;

class AnnotationHolder {
    final Map<String, IRunnable<?>> serverCommands = new HashMap<>();
    final Map<String, IRunnableWithResult<?>> serverCommandsWithResult = new HashMap<>();
    final Map<String, IRunnable<?>> clientCommands = new HashMap<>();
    final Map<String, IRunnableWithListResult<?, ?>> serverCommandsWithListResult = new HashMap<>();
    final Map<String, IRunnableWithList<?, ?>> clientCommandsWithList = new HashMap<>();
    final Map<String, ValueHolder<?, ?>> valueMap = new HashMap<>();
}
