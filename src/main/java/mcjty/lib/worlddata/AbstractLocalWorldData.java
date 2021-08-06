package mcjty.lib.worlddata;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Local world data
 */
public abstract class AbstractLocalWorldData<T extends AbstractLocalWorldData<T>> extends SavedData {

    private final String name;

    protected AbstractLocalWorldData(String name) {
        super();
        this.name = name;
    }

    public void save() {
        setDirty();
    }

    @Nonnull
    public static <T extends AbstractLocalWorldData<T>> T getData(Level world, Function<CompoundTag, T> reader, Supplier<T> supplier, String name) {
        if (world.isClientSide) {
            throw new RuntimeException("Don't access this client-side!");
        }
        DimensionDataStorage storage = ((ServerLevel)world).getDataStorage();
        return storage.computeIfAbsent(reader, supplier, name);
    }

}
