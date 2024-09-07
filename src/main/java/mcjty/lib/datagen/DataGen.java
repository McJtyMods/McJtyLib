package mcjty.lib.datagen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import mcjty.lib.crafting.CopyNBTRecipeBuilder;
import mcjty.lib.crafting.IRecipeBuilder;
import mcjty.lib.varia.Tools;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class DataGen {

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

        generator.addProvider(event.includeServer(), new GlobalLootModifierProvider(generator.getPackOutput(), modid) {
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
                Map<ResourceLocation, Object> entries = dob.codecObjectSupplier().getOrDefault(entry.getKey(), Collections::emptyMap).get();
                if (!entries.isEmpty()) {
                    generator.addProvider(event.includeServer(), new JsonCodecProvider<>(generator.getPackOutput(), event.getExistingFileHelper(),
                            modid, JsonOps.INSTANCE, PackType.SERVER_DATA, entry.getValue().directory(), entry.getValue().codec(), entries));
                }
            }
        }

        generator.addProvider(event.includeServer(), new BaseRecipeProvider(generator) {

            @Override
            protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
                for (Dob dob : dobs) {
                    dob.recipe().accept(new IRecipeFactory() {
                        @Override
                        public void recipe(Supplier<IRecipeBuilder> supplier) {
                            supplier.get().build(consumer);
                        }

                        @Override
                        public void recipeConsumer(Supplier<Consumer<Consumer<FinishedRecipe>>> consumerSupplier) {
                            consumerSupplier.get().accept(consumer);
                        }

                        @Override
                        public void recipe(String id, Supplier<IRecipeBuilder> supplier) {
                            supplier.get().build(consumer, new ResourceLocation(modid, id));
                        }

                        @Override
                        public void shapedNBT(CopyNBTRecipeBuilder builder, String... pattern) {
                            build(consumer, builder, pattern);
                        }

                        @Override
                        public void shapedNBT(String id, CopyNBTRecipeBuilder builder, String... pattern) {
                            build(consumer, new ResourceLocation(modid, id), builder, pattern);

                        }

                        @Override
                        public void shaped(ShapedRecipeBuilder builder, String... pattern) {
                            build(consumer, builder, pattern);
                        }

                        @Override
                        public void shaped(String id, ShapedRecipeBuilder builder, String... pattern) {
                            build(consumer, new ResourceLocation(modid, id), builder, pattern);
                        }

                        @Override
                        public void shapeless(ShapelessRecipeBuilder builder) {
                            build(consumer, builder);
                        }

                        @Override
                        public void shapeless(String id, ShapelessRecipeBuilder builder) {
                            build(consumer, new ResourceLocation(modid, id), builder);
                        }
                    });
                }
            }
        });

        // @todo 1.19.3, probably not right
        BaseLootTableProvider lootTableProvider = new BaseLootTableProvider();

        List<LootTableProvider.SubProviderEntry> list = List.of(new LootTableProvider.SubProviderEntry(() -> builder -> {
                    for (Dob dob : dobs) {
                        if (dob.blockSupplier() != null) {
                            dob.loot().accept(lootTableProvider);
                        }
                    }
                    lootTableProvider.generate(builder, LootContextParamSets.BLOCK);
                }, LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(() -> builder -> {
                    for (Dob dob : dobs) {
                        dob.loot().accept(lootTableProvider);
                    }
                    lootTableProvider.generate(builder, LootContextParamSets.CHEST);
                }, LootContextParamSets.CHEST),
                new LootTableProvider.SubProviderEntry(() -> biConsumer -> {
                    for (Dob dob : dobs) {
                        if (dob.entitySupplier() != null) {
                            dob.loot().accept(lootTableProvider);
                        }
                    }
                    lootTableProvider.generate(biConsumer, LootContextParamSets.ENTITY);
                }, LootContextParamSets.ENTITY));
        generator.addProvider(event.includeServer(), new LootTableProvider(generator.getPackOutput(), Collections.emptySet(),
                list));


//        generator.addProvider(event.includeServer(), new BaseLootTableProvider(generator) {
//            @Override
//            protected void addTables() {
//                for (Dob dob : dobs) {
//                    dob.loot().accept(this);
//                }
//            }
//        });
        BaseBlockTagsProvider blockTags = new BaseBlockTagsProvider(generator, event.getLookupProvider(), modid, event.getExistingFileHelper()) {
            @Override
            protected void addTags(HolderLookup.Provider provider) {
                for (Dob dob : dobs) {
                    dob.blockTags().accept(new ITagFactory() {
                        @Override
                        public void blockTags(Supplier<? extends Block> blockSupplier, List<TagKey> tags) {
                            for (TagKey<Block> tag : tags) {
                                tag(tag).add(blockSupplier.get());
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
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new ItemTagsProvider(generator.getPackOutput(), event.getLookupProvider(), blockTags.contentsGetter(), modid, event.getExistingFileHelper()) {
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
                                tag(tag).add(itemSupplier.get());
                            }
                        }
                    });
                }
            }
        });

        generator.addProvider(event.includeClient(), new LanguageProvider(generator.getPackOutput(), modid, "en_us") {
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
                            key = Util.makeDescriptionId("message", ForgeRegistries.BLOCKS.getKey(dob.blockSupplier().get()));
                        } else if (dob.itemSupplier() != null) {
                            key = Util.makeDescriptionId("message", ForgeRegistries.ITEMS.getKey(dob.itemSupplier().get()));
                        } else if (dob.entitySupplier() != null) {
                            key = Util.makeDescriptionId("message", ForgeRegistries.ENTITY_TYPES.getKey(dob.entitySupplier().get()));
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
        generator.addProvider(event.includeClient(), new BaseBlockStateProvider(generator, modid, event.getExistingFileHelper()) {
            @Override
            protected void registerStatesAndModels() {
                for (Dob dob : dobs) {
                    dob.blockstate().accept(this);
                }
            }
        });
        generator.addProvider(event.includeClient(), new BaseItemModelProvider(generator, modid, event.getExistingFileHelper()) {
            @Override
            protected void registerModels() {
                BaseItemModelProvider provider = this;
                for (Dob dob : dobs) {
                    dob.item().accept(provider);
                }
            }
        });
    }

    public static InventoryChangeTrigger.TriggerInstance has(ItemLike item) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(item).build());
    }

    public static InventoryChangeTrigger.TriggerInstance has(TagKey<Item> tag) {
        return inventoryTrigger(ItemPredicate.Builder.item().of(tag).build());
    }

    public static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate... itemPredicate) {
        return new InventoryChangeTrigger.TriggerInstance(ContextAwarePredicate.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, itemPredicate);
    }

    record CodecProvider(String directory, Codec codec) {

    }
}
