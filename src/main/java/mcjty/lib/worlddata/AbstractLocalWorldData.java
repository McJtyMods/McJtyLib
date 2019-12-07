package mcjty.lib.worlddata;

import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Local world data
 */
public abstract class AbstractLocalWorldData<T extends AbstractLocalWorldData<T>> extends WorldSavedData {

    protected AbstractLocalWorldData(String name) {
        super(name);
    }

    public void save() {
        markDirty();
    }

    @Nonnull
    public static <T extends AbstractLocalWorldData<T>> T getData(World world, Supplier<? extends T> supplier, String name) {
        if (world.isRemote) {
            throw new RuntimeException("Don't access this client-side!");
        }
        DimensionSavedDataManager storage = ((ServerWorld)world).getSavedData();
        return storage.getOrCreate(supplier, name);
    }

}
