package mcjty.lib.datagen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.*;
import net.minecraft.resources.Identifier;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class CodecDataProvider<T> implements DataProvider {

    private final PackOutput.PathProvider path;
    private final Codec<T> codec;
    private final CompletableFuture<HolderLookup.Provider> registries;
    private final Map<Identifier,T> entries;

    public CodecDataProvider(PackOutput output,String directory,Codec<T> codec,CompletableFuture<HolderLookup.Provider> registries,Map<Identifier,T> entries) {
        this.path=output.createPathProvider(PackOutput.Target.DATA_PACK,directory);
        this.codec=codec;
        this.registries=registries;
        this.entries=Map.copyOf(entries);
    }

    public String getName() {
        return "McJtyLib codec "+path;
    }

    public CompletableFuture<?> run(CachedOutput cache) {
        return registries.thenCompose(lookup->CompletableFuture.allOf(entries.entrySet().stream().map(e->DataProvider.saveStable(cache,codec.encodeStart(lookup.createSerializationContext(JsonOps.INSTANCE),e.getValue()).getOrThrow(),path.json(e.getKey()))).toArray(CompletableFuture[]::new)));
    }

}
