package mcjty.lib.worlddata;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractWorldData<T extends AbstractWorldData<T>> extends WorldSavedData {

    private static final Map<String, AbstractWorldData<?>> instances = new HashMap<>();

    protected AbstractWorldData(String name) {
        super(name);
    }

    public void save() {
        markDirty();
    }

    public abstract void clear();

    public static void clearInstances() {
        for (AbstractWorldData<?> data : instances.values()) {
            data.clear();
        }
        instances.clear();
    }

    public static int getDataCount() {
        return instances.size();
    }

    private static <T extends AbstractWorldData<T>> T getData(Supplier<? extends T> supplier, String name) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ServerWorld world = DimensionManager.getWorld(server, DimensionType.OVERWORLD, false, false);
        DimensionSavedDataManager storage = world.getSavedData();
        T data = storage.func_215752_a(supplier, name);
        return data;
    }

    @Nonnull
    public static <T extends AbstractWorldData<T>> T getData(World world, Supplier<? extends T> supplier, String name) {
        if (world.isRemote) {
            throw new RuntimeException("Don't access this client-side!");
        }

        T data = (T) instances.get(name);
        if (data != null) {
            return data;
        }
        data = getData(supplier, name);
        instances.put(name, data);
        return data;
    }

}
