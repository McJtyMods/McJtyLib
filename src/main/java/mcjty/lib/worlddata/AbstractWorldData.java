package mcjty.lib.worlddata;

import mcjty.lib.varia.LevelTools;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.SavedDataStorage;
import net.minecraft.world.level.saveddata.SavedData;
import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Global world data (always attached to the overworld)
 */
public abstract class AbstractWorldData<T extends AbstractWorldData<T>> extends SavedData {

    protected AbstractWorldData() {
        super();
    }

    public void save() {
        setDirty();
    }

    @Nonnull
    public static <T extends AbstractWorldData<T>> T getData(Level world, Function<CompoundTag, T> loader, Supplier<T> supplier, String name) {
        if (world.isClientSide()) {
            throw new RuntimeException("Don't access this client-side!");
        }
        SavedDataStorage storage = LevelTools.getOverworld(world).getDataStorage();
        var codec = CompoundTag.CODEC.xmap(loader, data -> data.save(new CompoundTag(), world.registryAccess()));
        return storage.computeIfAbsent(new net.minecraft.world.level.saveddata.SavedDataType<>(net.minecraft.resources.Identifier.parse(name), supplier, codec, null));
    }

    public abstract CompoundTag save(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider);
}
