package mcjty.lib.datagen;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.data.registries.RegistryPatchGenerator;
import net.minecraft.resources.RegistryDataLoader;

public final class ReloadableDataProvider implements DataProvider {

    private final DataProvider delegate;

    public ReloadableDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, RegistrySetBuilder entries) {
        var reloadableKeys = DynamicRegistries.getReloadableRegistries().stream()
                .map(RegistryDataLoader.RegistryData::key).collect(Collectors.toSet());
        var world = registries.thenApply(lookup -> HolderLookup.Provider.create(lookup.listRegistries()
                .filter(registry -> !reloadableKeys.contains(registry.key()))));
        var patched = RegistryPatchGenerator.createReloadableLookup(world, registries, entries)
                .thenApply(RegistrySetBuilder.PatchedRegistries::patches);
        delegate = RegistriesDatapackGenerator.forReloadableLayer(output, patched);
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return delegate.run(output);
    }
}
