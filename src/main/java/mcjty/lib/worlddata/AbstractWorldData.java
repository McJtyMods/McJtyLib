package mcjty.lib.worlddata;

import mcjty.lib.varia.LevelTools;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Global world data (always attached to the overworld)
 */
public abstract class AbstractWorldData<T extends AbstractWorldData<T>> extends SavedData {

    protected AbstractWorldData(String name) {
        super(name);
    }

    public void save() {
        setDirty();
    }

    @Nonnull
    public static <T extends AbstractWorldData<T>> T getData(Level world, Supplier<? extends T> supplier, String name) {
        if (world.isClientSide) {
            throw new RuntimeException("Don't access this client-side!");
        }
        DimensionDataStorage storage = LevelTools.getOverworld(world).getDataStorage();
        return storage.computeIfAbsent(supplier, name);
    }

}
