package mcjty.lib.worlddata;

import mcjty.lib.varia.WorldTools;
import net.minecraft.world.World;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

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
    public static <T extends AbstractWorldData<T>> T getData(World world, Supplier<? extends T> supplier, String name) {
//        if (world.isRemote) {
//            throw new RuntimeException("Don't access this client-side!");
//        }
        DimensionSavedDataManager storage = WorldTools.getOverworld(world).getSavedData();
        return storage.getOrCreate(supplier, name);
    }

}
