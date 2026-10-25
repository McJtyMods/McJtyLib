package mcjty.lib.datagen;

import com.mojang.serialization.Codec;
import mcjty.lib.crafting.CopyComponentsRecipeBuilder;
import mcjty.lib.crafting.IRecipeBuilder;
import net.minecraft.util.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider.ItemTagsProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DataGen implements DataProvider {

    private static final ThreadLocal<net.minecraft.core.HolderGetter<Item>> RECIPE_ITEMS = new ThreadLocal<>();

    /** Registry lookup for recipe callbacks, including tags introduced by data generation. */
    public static net.minecraft.core.HolderGetter<Item> recipeItems() {
        var items = RECIPE_ITEMS.get();
        return items == null ? BuiltInRegistries.ITEM : items;
    }

    private static void withRecipeItems(net.minecraft.core.HolderGetter<Item> items, Runnable recipes) {
        var previous = RECIPE_ITEMS.get();
        RECIPE_ITEMS.set(items);
        try {
            recipes.run();
        } finally {
            if (previous == null) RECIPE_ITEMS.remove(); else RECIPE_ITEMS.set(previous);
        }
    }

    private final String modid;
    private final FabricPackOutput output;
    private final CompletableFuture<HolderLookup.Provider> registries;
    private final List<DataProvider> providers = new ArrayList<>();
    private final List<Dob> dobs = new ArrayList<>();
    private final Map<String, CodecProvider> codecProviders = new HashMap<>();

    public DataGen(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.modid=output.getModId();this.output=output;this.registries=registries;
    }
    @Override public String getName(){return modid+" McJtyLib data";}
    @Override public CompletableFuture<?> run(CachedOutput cache){return CompletableFuture.allOf(providers.stream().map(provider->provider.run(cache)).toArray(CompletableFuture[]::new));}


    public void addCodecProvider(String name, String directory, Codec codec) {
        codecProviders.put(name, new CodecProvider(directory, codec));
    }

    public void add(Dob.Builder... builder) {
        for (Dob.Builder b : builder) {
            dobs.add(b.build());
        }
    }

    public void generate() {
        Map<Identifier,net.minecraft.world.level.storage.loot.functions.LootItemFunction> globalFunctions=new HashMap<>();
        for(Dob dob:dobs)dob.glmSupplier().forEach((id,factory)->globalFunctions.put(Identifier.fromNamespaceAndPath(modid,id),factory.get()));
        if(!globalFunctions.isEmpty())providers.add(new CodecDataProvider<>(output,"mcjtylib/loot_functions",net.minecraft.world.level.storage.loot.functions.LootItemFunctions.ROOT_CODEC,registries,globalFunctions));

        for (Map.Entry<String, CodecProvider> entry : codecProviders.entrySet()) {
            for (Dob dob : dobs) {
                HolderLookup.Provider provider = dob.holderLookupSupplier().get(entry.getKey());
                Map<Identifier, Object> entries = dob.codecObjectSupplier().getOrDefault(entry.getKey(), Collections::emptyMap).get();
                if (!entries.isEmpty()) {
                    providers.add(new CodecDataProvider<>(output,entry.getValue().directory(),entry.getValue().codec(),CompletableFuture.completedFuture(provider),entries));
                }
            }
        }

        providers.add( new net.minecraft.data.recipes.RecipeProvider.Runner(output, registries) {
            @Override public String getName() { return modid + " recipes"; }
            @Override protected net.minecraft.data.recipes.RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
                return new BaseRecipeProvider(registries, output) {

                    @Override
                    public void buildRecipes() {
                        RecipeOutput consumer = output;
                        withRecipeItems(items, () -> {
                            for (Dob dob : dobs) {
                                dob.recipe().accept(new IRecipeFactory() {
                                    @Override public net.minecraft.core.HolderGetter<Item> items() { return items; }
                                    @Override
                                    public void recipe(Supplier<IRecipeBuilder> supplier) {
                                        supplier.get().build(consumer);
                                    }

                                    @Override
                                    public void recipeConsumer(Supplier<Consumer<RecipeOutput>> consumerSupplier) {
                                        consumerSupplier.get().accept(consumer);
                                    }

                                    @Override
                                    public void recipe(String id, Supplier<IRecipeBuilder> supplier) {
                                        supplier.get().build(consumer, Identifier.fromNamespaceAndPath(modid, id));
                                    }

                                    @Override
                                    public void shapedNBT(CopyComponentsRecipeBuilder builder, String... pattern) {
                                        build(consumer, builder, pattern);
                                    }

                                    @Override
                                    public void shapedNBT(String id, CopyComponentsRecipeBuilder builder, String... pattern) {
                                        build(consumer, Identifier.fromNamespaceAndPath(modid, id), builder, pattern);

                                    }

                                    @Override
                                    public void shaped(ShapedRecipeBuilder builder, String... pattern) {
                                        build(consumer, builder, pattern);
                                    }

                                    @Override
                                    public void shaped(String id, ShapedRecipeBuilder builder, String... pattern) {
                                        build(consumer, Identifier.fromNamespaceAndPath(modid, id), builder, pattern);
                                    }

                                    @Override
                                    public void shapeless(ShapelessRecipeBuilder builder) {
                                        build(consumer, builder);
                                    }

                                    @Override
                                    public void shapeless(String id, ShapelessRecipeBuilder builder) {
                                        build(consumer, Identifier.fromNamespaceAndPath(modid, id), builder);
                                    }
                                });
                            }
                        });
                    }
                };
            }
        });

        // @todo 1.19.3, probably not right
        BaseLootTableProvider lootTableProvider = new BaseLootTableProvider();

        List<LootTableProvider.SubProviderEntry> list = List.of(new LootTableProvider.SubProviderEntry(provider -> builder -> {
                    for (Dob dob : dobs) {
                        if (dob.blockSupplier() != null) {
                            dob.loot().accept(lootTableProvider);
                        }
                    }
                    lootTableProvider.generate(builder, LootContextParamSets.BLOCK);
                }, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(provider -> builder -> {
                    for (Dob dob : dobs) {
                        dob.loot().accept(lootTableProvider);
                    }
                    lootTableProvider.generate(builder, LootContextParamSets.CHEST);
                }, LootContextParamSets.CHEST),
                new LootTableProvider.SubProviderEntry(provider -> builder -> {
                    for (Dob dob : dobs) {
                        if (dob.entitySupplier() != null) {
                            dob.loot().accept(lootTableProvider);
                        }
                    }
                    lootTableProvider.generate(builder, LootContextParamSets.ENTITY);
                }, LootContextParamSets.ENTITY));
        providers.add( new LootTableProvider(output, Collections.emptySet(),
                list, registries));


//        providers.add( new BaseLootTableProvider(generator) {
//            @Override
//            protected void addTables() {
//                for (Dob dob : dobs) {
//                    dob.loot().accept(this);
//                }
//            }
//        });
        BaseBlockTagsProvider blockTags = new BaseBlockTagsProvider(output, registries, modid) {
            @Override
            protected void addTags(HolderLookup.Provider provider) {
                for (Dob dob : dobs) {
                    dob.blockTags().accept(new ITagFactory() {
                        @Override
                        public void blockTags(Supplier<? extends Block> blockSupplier, List<TagKey> tags) {
                            for (TagKey<Block> tag : tags) {
                                builder(tag).add(BuiltInRegistries.BLOCK.getResourceKey(blockSupplier.get()).orElseThrow());
                            }
                        }

                        @Override
                        public void itemTags(Supplier<? extends Item> itemSupplier, List<TagKey> tags) {
                            // No op
                        }
                    });
                }
            }
        };
        providers.add( blockTags);
        providers.add( new ItemTagsProvider(output, registries) {
            @Override
            protected void addTags(HolderLookup.Provider provider) {
                for (Dob dob : dobs) {
                    dob.itemTags().accept(new ITagFactory() {
                        @Override
                        public void blockTags(Supplier<? extends Block> blockSupplier, List<TagKey> tags) {
                            // No op
                        }

                        @Override
                        public void itemTags(Supplier<? extends Item> itemSupplier, List<TagKey> tags) {
                            for (TagKey<Item> tag : tags) {
                                builder(tag).add(BuiltInRegistries.ITEM.getResourceKey(itemSupplier.get()).orElseThrow());
                            }
                        }
                    });
                }
            }
        });

        providers.add( new LanguageProvider(output, "en_us", registries) {
            @Override
            protected void addTranslations() {
                for (Dob dob : dobs) {
                    String name = dob.translatedName();
                    if (name != null) {
                        if (dob.blockSupplier() != null) {
                            this.add(dob.blockSupplier().get(), name);
                        } else if (dob.itemSupplier() != null) {
                            this.add(dob.itemSupplier().get(), name);
                        } else if (dob.entitySupplier() != null) {
                            this.add(dob.entitySupplier().get(), name);
                        }
                    }
                    Map<String, String> keyedMessages = dob.keyedMessages();
                    for (Map.Entry<String, String> entry : keyedMessages.entrySet()) {
                        String key;
                        if (dob.blockSupplier() != null) {
                            key = Util.makeDescriptionId("message", BuiltInRegistries.BLOCK.getKey(dob.blockSupplier().get()));
                        } else if (dob.itemSupplier() != null) {
                            key = Util.makeDescriptionId("message", BuiltInRegistries.ITEM.getKey(dob.itemSupplier().get()));
                        } else if (dob.entitySupplier() != null) {
                            key = Util.makeDescriptionId("message", BuiltInRegistries.ENTITY_TYPE.getKey(dob.entitySupplier().get()));
                        } else {
                            throw new RuntimeException("Not supported!");
                        }
                        this.add(key + "." + entry.getKey(), entry.getValue());
                    }
                    Map<String, String> messages = dob.messages();
                    for (Map.Entry<String, String> entry : messages.entrySet()) {
                        this.add(entry.getKey(), entry.getValue());
                    }
                }
            }
        });
        providers.add( new BaseBlockStateProvider(output, modid) {
            @Override
            protected void registerStatesAndModels() {
                for (Dob dob : dobs) {
                    dob.blockstate().accept(this);
                }
            }
        });
        providers.add( new BaseItemModelProvider(output, modid) {
            @Override
            protected void registerModels() {
                BaseItemModelProvider provider = this;
                for (Dob dob : dobs) {
                    dob.item().accept(provider);
                }
            }
        });
    }

    public static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike item) {
        return CriteriaTriggers.INVENTORY_CHANGED.createCriterion(inventoryTrigger(ItemPredicate.Builder.item().of(recipeItems(), item).build()));
    }

    public static Criterion<InventoryChangeTrigger.TriggerInstance> has(TagKey<Item> tag) {
        return CriteriaTriggers.INVENTORY_CHANGED.createCriterion(inventoryTrigger(ItemPredicate.Builder.item().of(recipeItems(), tag).build()));
    }

    public static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate... itemPredicate) {
        return new InventoryChangeTrigger.TriggerInstance(Optional.empty(), InventoryChangeTrigger.TriggerInstance.Slots.ANY, List.of(itemPredicate));
    }

    record CodecProvider(String directory, Codec codec) {

    }
}
