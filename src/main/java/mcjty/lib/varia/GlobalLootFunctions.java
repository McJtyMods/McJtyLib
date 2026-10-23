package mcjty.lib.varia;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.storage.loot.functions.*;
import java.util.*;

/** Datapack functions applied to generated drops, including tables supplied by other mods. */
public final class GlobalLootFunctions {

    private static volatile Map<net.minecraft.resources.Identifier,JsonElement> definitions=Map.of();

    private record Parsed(Map<?,?> definitions,HolderLookup.Provider lookup,List<LootItemFunction> functions) {
    }

    private static volatile Parsed parsed;

    public static void register() {

        LootTableEvents.ALL_LOADED.register((resources,registry)-> {

            var loaded=new LinkedHashMap<net.minecraft.resources.Identifier,JsonElement>();
            resources.listResources("mcjtylib/loot_functions",id->id.getPath().endsWith(".json")).entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry-> {

                try(var reader=entry.getValue().openAsReader()) {
                    loaded.put(entry.getKey(),JsonParser.parseReader(reader));
                }

                catch(Exception error) {
                    throw new IllegalStateException("Cannot load global loot function "+entry.getKey(),error);
                }

            });definitions=Collections.unmodifiableMap(loaded);parsed=null;
        });

        LootTableEvents.MODIFY_DROPS.register((holder,context,drops)-> {

            var current=definitions;if(current.isEmpty())return;
            var lookup=context.getLevel().getServer().reloadableRegistries().lookup();var cached=parsed;
            if(cached==null||cached.definitions()!=current||cached.lookup()!=lookup) {

                var ops=lookup.createSerializationContext(JsonOps.INSTANCE);var functions=new ArrayList<LootItemFunction>();
                current.forEach((id,json)-> {
                    try {
                        functions.add(LootItemFunctions.DIRECT_CODEC.parse(ops,json).getOrThrow());
                    }
                    catch(Exception error) {
                        throw new IllegalStateException("Invalid global loot function "+id,error);
                    }
                });
                cached=new Parsed(current,lookup,List.copyOf(functions));parsed=cached;
            }

            for(var function:cached.functions())for(int i=0;i<drops.size();i++)drops.set(i,function.apply(drops.get(i),context));
            drops.removeIf(net.minecraft.world.item.ItemStack::isEmpty);
        });

    }

}
