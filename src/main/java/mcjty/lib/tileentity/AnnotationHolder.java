package mcjty.lib.tileentity;

import mcjty.lib.blockcommands.IRunnable;
import mcjty.lib.blockcommands.IRunnableWithList;
import mcjty.lib.blockcommands.IRunnableWithListResult;
import mcjty.lib.blockcommands.IRunnableWithResult;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class AnnotationHolder {

    // This is static but that is not a big problem since when a new world loads this remains valid
    static final Map<BlockEntityType, AnnotationHolder> annotations = new HashMap<>();

    final Map<String, IRunnable<?>> serverCommands = new HashMap<>();
    final Map<String, IRunnableWithResult<?>> serverCommandsWithResult = new HashMap<>();
    final Map<String, IRunnable<?>> clientCommands = new HashMap<>();
    final Map<String, IRunnableWithListResult<?, ?>> serverCommandsWithListResult = new HashMap<>();
    final Map<String, IRunnableWithList<?, ?>> clientCommandsWithList = new HashMap<>();
    final Map<String, ValueHolder<?, ?>> valueMap = new HashMap<>();
    final List<Pair<Field, Cap>> capabilityList = new ArrayList<>();
}
