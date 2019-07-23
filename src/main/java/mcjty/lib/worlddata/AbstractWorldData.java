package mcjty.lib.worlddata;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public abstract class AbstractWorldData<T extends AbstractWorldData<T>> extends WorldSavedData {

    protected AbstractWorldData(String name) {
        super(name);
    }

    public void save() {
        markDirty();
    }

    @Nonnull
    public static <T extends AbstractWorldData<T>> T getData(Supplier<? extends T> supplier, String name) {
//        if (world.isRemote) {
//            throw new RuntimeException("Don't access this client-side!");
//        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ServerWorld world1 = DimensionManager.getWorld(server, DimensionType.OVERWORLD, false, false);
        DimensionSavedDataManager storage = world1.getSavedData();
        return storage.getOrCreate(supplier, name);
    }

}
