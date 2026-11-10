package mcjty.lib.varia;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import java.util.*;
import java.util.IdentityHashMap;
import java.util.List;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.*;

/** Datapack functions applied to generated drops, including tables supplied by other mods. */
public final class GlobalLootFunctions {

    private static volatile Map<ResourceLocation, JsonElement> definitions = Map.of();

    private record Parsed(
            Map<?, ?> definitions,
            HolderLookup.Provider lookup,
            List<LootItemFunction> functions) {}

    private static volatile Parsed parsed;
    private static volatile Map<LootTable, ResourceLocation> tableIds = Map.of();

    public static ResourceLocation tableId(LootTable table) {
        return tableIds.get(table);
    }

    public static void register() {

        LootTableEvents.ALL_LOADED.register(
                (resources, registry) -> {
                    var ids = new IdentityHashMap<LootTable, ResourceLocation>();
                    registry.entrySet()
                            .forEach(entry -> ids.put(entry.getValue(), entry.getKey().location()));
                    tableIds = Collections.unmodifiableMap(ids);

                    var loaded = new LinkedHashMap<ResourceLocation, JsonElement>();
                    resources
                            .listResources(
                                    "mcjtylib/loot_functions", id -> id.getPath().endsWith(".json"))
                            .entrySet()
                            .stream()
                            .sorted(Map.Entry.comparingByKey())
                            .forEach(
                                    entry -> {
                                        try (var reader = entry.getValue().openAsReader()) {
                                            loaded.put(
                                                    entry.getKey(), JsonParser.parseReader(reader));
                                        } catch (Exception error) {
                                            throw new IllegalStateException(
                                                    "Cannot load global loot function "
                                                            + entry.getKey(),
                                                    error);
                                        }
                                    });
                    definitions = Collections.unmodifiableMap(loaded);
                    parsed = null;
                });
    }

    public static void modify(LootContext context, List<ItemStack> drops) {

        var current = definitions;
        if (current.isEmpty()) return;
        var lookup = context.getLevel().registryAccess();
        var cached = parsed;
        if (cached == null || cached.definitions() != current || cached.lookup() != lookup) {

            var ops = lookup.createSerializationContext(JsonOps.INSTANCE);
            var functions = new ArrayList<LootItemFunction>();
            current.forEach(
                    (id, json) -> {
                        try {
                            functions.add(
                                    LootItemFunctions.ROOT_CODEC.parse(ops, json).getOrThrow());
                        } catch (Exception error) {
                            throw new IllegalStateException(
                                    "Invalid global loot function " + id, error);
                        }
                    });
            cached = new Parsed(current, lookup, List.copyOf(functions));
            parsed = cached;
        }

        for (var function : cached.functions())
            for (int i = 0; i < drops.size(); i++)
                drops.set(i, function.apply(drops.get(i), context));
        drops.removeIf(ItemStack::isEmpty);
    }
}
