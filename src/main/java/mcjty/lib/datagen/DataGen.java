package mcjty.lib.datagen;

import com.mojang.serialization.Codec;
import mcjty.lib.crafting.CopyComponentsRecipeBuilder;
import mcjty.lib.crafting.IRecipeBuilder;
import net.minecraft.util.Util;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.data.JsonCodecProvider;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DataGen {

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
    private final GatherDataEvent event;
    private final List<Dob> dobs = new ArrayList<>();
    private final Map<String, CodecProvider> codecProviders = new HashMap<>();

    public DataGen(String modid, GatherDataEvent event) {
        this.modid = modid;
        this.event = event;
    }

    public void addCodecProvider(String name, String directory, Codec codec) {
        codecProviders.put(name, new CodecProvider(directory, codec));
    }

    public void add(Dob.Builder... builder) {
        for (Dob.Builder b : builder) {
            dobs.add(b.build());
        }
    }

    public void generate() {
        DataGenerator generator = event.getGenerator();

        generator.addProvider(true, new GlobalLootModifierProvider(generator.getPackOutput(), event.getWorldLookupProvider(), modid) {
            @Override
            protected void start() {
                for (Dob dob : dobs) {
                    for (Map.Entry<String, Supplier<IGlobalLootModifier>> entry : dob.glmSupplier().entrySet()) {
                        add(entry.getKey(), entry.getValue().get());
                    }
                }
            }
        });


        for (Map.Entry<String, CodecProvider> entry : codecProviders.entrySet()) {
            for (Dob dob : dobs) {
                HolderLookup.Provider provider = dob.holderLookupSupplier().get(entry.getKey());
                Map<Identifier, Object> entries = dob.codecObjectSupplier().getOrDefault(entry.getKey(), Collections::emptyMap).get();
                if (!entries.isEmpty()) {
                    JsonCodecProvider codecProvider = new JsonCodecProvider<>(generator.getPackOutput(), PackOutput.Target.DATA_PACK, entry.getValue().directory(),
                            entry.getValue().codec(), CompletableFuture.completedFuture(provider), modid) {
                        @Override
                        protected void gather() {

                        }
                    };
                    entries.forEach(codecProvider::unconditional);
                    generator.addProvider(true, codecProvider);
                }
            }
        }

        var reloadableEntries = new net.minecraft.core.RegistrySetBuilder();
        reloadableEntries.add(net.minecraft.data.recipes.RecipeProvider.asBootstrap((recipes, advancements) ->
                new BaseRecipeProvider(recipes, advancements) {

                    @Override
                    protected void buildRecipes() {
                        RecipeOutput consumer = this.output;
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
                }));

        // @todo 1.19.3, probably not right
        BaseLootTableProvider lootTableProvider = new BaseLootTableProvider();

        List<LootTableProvider.SubProviderEntry> list = List.of(new LootTableProvider.SubProviderEntry(context -> () -> {
                    for (Dob dob : dobs) {
                        if (dob.blockSupplier() != null) {
                            dob.loot().accept(lootTableProvider);
                        }
                    }
                    lootTableProvider.generate(context::accept, LootContextParamSets.BLOCK);
                }, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(context -> () -> {
                    for (Dob dob : dobs) {
                        dob.loot().accept(lootTableProvider);
                    }
                    lootTableProvider.generate(context::accept, LootContextParamSets.CHEST);
                }, LootContextParamSets.CHEST),
                new LootTableProvider.SubProviderEntry(context -> () -> {
                    for (Dob dob : dobs) {
                        if (dob.entitySupplier() != null) {
                            dob.loot().accept(lootTableProvider);
                        }
                    }
                    lootTableProvider.generate(context::accept, LootContextParamSets.ENTITY);
                }, LootContextParamSets.ENTITY));
        reloadableEntries.add(net.minecraft.core.registries.Registries.LOOT_TABLE,
                new LootTableProvider(Collections.emptySet(), list));
        event.getDefaultPackGenerator().createReloadableRegistryObjects(reloadableEntries);


//        generator.addProvider(true, new BaseLootTableProvider(generator) {
//            @Override
//            protected void addTables() {
//                for (Dob dob : dobs) {
//                    dob.loot().accept(this);
//                }
//            }
//        });
        BaseBlockTagsProvider blockTags = new BaseBlockTagsProvider(generator, event.getWorldLookupProvider(), modid) {
            @Override
            protected void addTags(HolderLookup.Provider provider) {
                for (Dob dob : dobs) {
                    dob.blockTags().accept(new ITagFactory() {
                        @Override
                        public void blockTags(Supplier<? extends Block> blockSupplier, List<TagKey> tags) {
                            for (TagKey<Block> tag : tags) {
                                tag(tag).add(BuiltInRegistries.BLOCK.getResourceKey(blockSupplier.get()).orElseThrow());
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
        generator.addProvider(true, blockTags);
        generator.addProvider(true, new ItemTagsProvider(generator.getPackOutput(), event.getWorldLookupProvider(), modid) {
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
                                tag(tag).add(BuiltInRegistries.ITEM.getResourceKey(itemSupplier.get()).orElseThrow());
                            }
                        }
                    });
                }
            }
        });

        generator.addProvider(event instanceof GatherDataEvent.Client, new LanguageProvider(generator.getPackOutput(), modid, "en_us") {
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
        generator.addProvider(event instanceof GatherDataEvent.Client, new BaseBlockStateProvider(generator, modid) {
            @Override
            protected void registerStatesAndModels() {
                for (Dob dob : dobs) {
                    dob.blockstate().accept(this);
                }
            }
        });
        generator.addProvider(event instanceof GatherDataEvent.Client, new BaseItemModelProvider(generator, modid) {
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
