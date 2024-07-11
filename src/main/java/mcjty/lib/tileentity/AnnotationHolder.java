package mcjty.lib.tileentity;

import mcjty.lib.blockcommands.IRunnable;
import mcjty.lib.blockcommands.IRunnableWithList;
import mcjty.lib.blockcommands.IRunnableWithListResult;
import mcjty.lib.blockcommands.IRunnableWithResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.IBlockCapabilityProvider;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationHolder {

    // This is static but that is not a big problem since when a new world loads this remains valid
    static final Map<Class<? extends GenericTileEntity>, AnnotationHolder> annotations = new HashMap<>();

    final Map<String, IRunnable<?>> serverCommands = new HashMap<>();
    final Map<String, IRunnableWithResult<?>> serverCommandsWithResult = new HashMap<>();
    final Map<String, IRunnable<?>> clientCommands = new HashMap<>();
    final Map<String, IRunnableWithListResult<?, ?>> serverCommandsWithListResult = new HashMap<>();
    final Map<String, IRunnableWithList<?, ?>> clientCommandsWithList = new HashMap<>();
    final Map<String, ValueHolder<?, ?>> valueMap = new HashMap<>();
//    final List<Pair<Field, Cap>> capabilityList = new ArrayList<>();
    final List<CapHolder> caps = new ArrayList<>();

    public record CapHolder<B, C>(BlockCapability<B, C> capability, IBlockCapabilityProvider<B, C> provider, DeferredBlock<?> block) {}

    public int getCapSize() {
        return caps.size();
    }

    public <B, C> CapHolder<B, C> getCapHolder(int i) {
        return caps.get(i);
    }
}
