package mcjty.lib.datagen;

import mcjty.lib.crafting.CopyNBTRecipeBuilder;
import mcjty.lib.crafting.IRecipeBuilder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public record Dob(
        Supplier<? extends Block> blockSupplier,
        Supplier<? extends Item> itemSupplier,
        Supplier<? extends EntityType> entitySupplier,
        Map<String, Supplier<Map<ResourceLocation, Object>>> codecObjectSupplier,
        Map<String, Supplier<IGlobalLootModifier>> glmSupplier,
        String translatedName,
        Map<String, String> keyedMessages,
        Map<String, String> messages,
        Consumer<BaseLootTableProvider> loot,
        Consumer<BaseBlockStateProvider> blockstate,
        Consumer<BaseItemModelProvider> item,
        Consumer<ITagFactory> blockTags,
        Consumer<ITagFactory> itemTags,
        Consumer<IRecipeFactory> recipe) {

    public static Builder builder() {
        return new Builder(null, null, null);
    }

    public static Builder builder(Supplier<? extends Block> blockSupplier, Supplier<? extends Item> itemSupplier) {
        return new Builder(blockSupplier, itemSupplier, null);
    }

    public static Builder blockBuilder(Supplier<? extends Block> blockSupplier) {
        return new Builder(blockSupplier, null, null);
    }

    public static Builder itemBuilder(Supplier<? extends Item> itemSupplier) {
        return new Builder(null, itemSupplier, null);
    }

    public static Builder entityBuilder(Supplier<? extends EntityType> entitySupplier) {
        return new Builder(null, null, entitySupplier);
    }

    public static class Builder {
        private final Supplier<? extends Block> blockSupplier;
        private final Supplier<? extends Item> itemSupplier;
        private final Supplier<? extends EntityType> entitySupplier;
        private final Map<String, Supplier<Map<ResourceLocation, Object>>> codecObjectSupplier = new HashMap<>();
        private final Map<String, Supplier<IGlobalLootModifier>> glmSupplier = new HashMap<>();
        private String translatedName = null;
        private Map<String, String> keyedMessages = new HashMap<>();
        private Map<String, String> messages = new HashMap<>();
        private Consumer<BaseLootTableProvider> loot = p -> { };
        private Consumer<BaseBlockStateProvider> blockstate = p -> { };
        private Consumer<BaseItemModelProvider> item = p -> { };
        private Consumer<ITagFactory> blockTags = f -> { };
        private Consumer<ITagFactory> itemTags = f -> { };
        private Consumer<IRecipeFactory> recipe = f -> { };

        public Builder(Supplier<? extends Block> blockSupplier, Supplier<? extends Item> itemSupplier, Supplier<? extends EntityType> entitySupplier) {
            this.blockSupplier = blockSupplier;
            this.itemSupplier = itemSupplier;
            this.entitySupplier = entitySupplier;
        }

        public Builder codecObjectSupplier(String name, Supplier<Map<ResourceLocation, Object>> supplier) {
            Supplier<Map<ResourceLocation, Object>> oldSupplier = codecObjectSupplier.get(name);
            if (oldSupplier == null) {
                codecObjectSupplier.put(name, supplier);
            } else {
                codecObjectSupplier.put(name, () -> {
                    Map<ResourceLocation, Object> old = new HashMap<>(oldSupplier.get());
                    old.putAll(supplier.get());
                    return old;
                });
            }
            return this;
        }

        public Builder glm(String lootName, Supplier<IGlobalLootModifier> modifier) {
            glmSupplier.put(lootName, modifier);
            return this;
        }

        public Builder name(String name) {
            this.translatedName = name;
            return this;
        }

        public Builder keyedMessage(String key, String message) {
            this.keyedMessages.put(key, message);
            return this;
        }

        public Builder message(String key, String message) {
            this.messages.put(key, message);
            return this;
        }

        public Builder loot(Consumer<BaseLootTableProvider> loot) {
            this.loot = loot;
            return this;
        }

        public Builder simpleLoot() {
            this.loot = p -> p.addSimpleTable(blockSupplier.get());
            return this;
        }

        public Builder standardLoot(@SuppressWarnings("rawtypes") Supplier be) {
            this.loot = f -> f.addStandardTable(blockSupplier.get(), (BlockEntityType<?>) be.get());
            return this;
        }

        public Builder silkTouchLoot(Supplier<Item> lootItem, float min, float max) {
            this.loot = f -> f.addLootTable(blockSupplier.get(), f.createSilkTouchTable("silk", blockSupplier.get(), lootItem.get(), min, max));
            return this;
        }

        public Builder blockState(Consumer<BaseBlockStateProvider> factory) {
            this.blockstate = factory;
            return this;
        }

        public Builder simpleBlockState() {
            this.blockstate = provider -> provider.simpleBlock(blockSupplier.get());
            return this;
        }

        public Builder itemModel(Consumer<BaseItemModelProvider> factory) {
            this.item = factory;
            return this;
        }

        public Builder handheldItem(String texture) {
            this.item = f -> f.itemHandheld(itemSupplier.get(), texture);
            return this;
        }

        public Builder generatedItem(String texture) {
            this.item = f -> f.itemGenerated(itemSupplier.get(), texture);
            return this;
        }

        public Builder cubeAll(ResourceLocation texture) {
            this.item = f -> f.cubeAll(f.name(itemSupplier.get()), texture);
            return this;
        }

        public Builder parentedItem(String model) {
            this.item = f -> f.parentedBlock(blockSupplier.get(), model);
            return this;
        }

        public Builder parentedItem() {
            this.item = f -> f.parentedBlock(blockSupplier.get());
            return this;
        }

        public Builder stonePickaxeTags() {
            final Consumer<ITagFactory> orig = this.blockTags;
            this.blockTags = f -> {
                orig.accept(f);
                f.blockTags(blockSupplier, List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_STONE_TOOL));
            };
            return this;
        }

        public Builder ironPickaxeTags() {
            final Consumer<ITagFactory> orig = this.blockTags;
            this.blockTags = f -> {
                orig.accept(f);
                f.blockTags(blockSupplier, List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_IRON_TOOL));
            };
            return this;
        }

        public Builder diamondPickaxeTags() {
            final Consumer<ITagFactory> orig = this.blockTags;
            this.blockTags = f -> {
                orig.accept(f);
                f.blockTags(blockSupplier, List.of(BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.NEEDS_DIAMOND_TOOL));
            };
            return this;
        }

        public Builder blockTags(@SuppressWarnings("rawtypes") List<TagKey> tags) {
            final Consumer<ITagFactory> orig = this.blockTags;
            this.blockTags = f -> {
                orig.accept(f);
                f.blockTags(blockSupplier, tags);
            };
            return this;
        }

        public Builder itemTags(@SuppressWarnings("rawtypes") List<TagKey> tags) {
            final Consumer<ITagFactory> orig = this.itemTags;
            this.itemTags = f -> {
                orig.accept(f);
                f.itemTags(itemSupplier, tags);
            };
            return this;
        }

        private ItemLike getItemLike() {
            if (blockSupplier == null) {
                return itemSupplier.get();
            } else {
                return blockSupplier.get();
            }
        }

        public Builder recipeConsumer(Supplier<Consumer<Consumer<FinishedRecipe>>> consumerSupplier) {
            final Consumer<IRecipeFactory> orig = this.recipe;
            this.recipe = f -> {
                orig.accept(f);
                f.recipeConsumer(consumerSupplier);
            };
            return this;
        }

        public Builder recipe(Supplier<IRecipeBuilder> recipe) {
            final Consumer<IRecipeFactory> orig = this.recipe;
            this.recipe = f -> {
                orig.accept(f);
                f.recipe(recipe);
            };
            return this;
        }

        public Builder recipe(String id, Supplier<IRecipeBuilder> recipe) {
            final Consumer<IRecipeFactory> orig = this.recipe;
            this.recipe = f -> {
                orig.accept(f);
                f.recipe(id, recipe);
            };
            return this;
        }

        public Builder shapedNBT(Function<CopyNBTRecipeBuilder, CopyNBTRecipeBuilder> builder, String... pattern) {
            final Consumer<IRecipeFactory> orig = this.recipe;
            this.recipe = f -> {
                orig.accept(f);
                f.shapedNBT(builder.apply(CopyNBTRecipeBuilder.shapedRecipe(getItemLike())), pattern);
            };
            return this;
        }

        public Builder shapedNBT(String id, Function<CopyNBTRecipeBuilder, CopyNBTRecipeBuilder> builder, String... pattern) {
            final Consumer<IRecipeFactory> orig = this.recipe;
            this.recipe = f -> {
                orig.accept(f);
                f.shapedNBT(id, builder.apply(CopyNBTRecipeBuilder.shapedRecipe(getItemLike())), pattern);
            };
            return this;
        }

        public Builder shaped(Function<ShapedRecipeBuilder, ShapedRecipeBuilder> builder, String... pattern) {
            final Consumer<IRecipeFactory> orig = this.recipe;
            this.recipe = f -> {
                orig.accept(f);
                // @todo 1.19.3
                f.shaped(builder.apply(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItemLike())), pattern);
            };
            return this;
        }

        public Builder shaped(Function<ShapedRecipeBuilder, ShapedRecipeBuilder> builder, int amount, String... pattern) {
            final Consumer<IRecipeFactory> orig = this.recipe;
            this.recipe = f -> {
                orig.accept(f);
                // @todo 1.19.3
                f.shaped(builder.apply(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItemLike(), amount)), pattern);
            };
            return this;
        }

        public Builder shaped(String id, Function<ShapedRecipeBuilder, ShapedRecipeBuilder> builder, String... pattern) {
            final Consumer<IRecipeFactory> orig = this.recipe;
            this.recipe = f -> {
                orig.accept(f);
                // @todo 1.19.3
                f.shaped(id, builder.apply(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItemLike())), pattern);
            };
            return this;
        }

        public Builder shaped(String id, Function<ShapedRecipeBuilder, ShapedRecipeBuilder> builder, int amount, String... pattern) {
            final Consumer<IRecipeFactory> orig = this.recipe;
            this.recipe = f -> {
                orig.accept(f);
                f.shaped(id, builder.apply(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, getItemLike(), amount)), pattern);
            };
            return this;
        }

        public Builder shapeless(Function<ShapelessRecipeBuilder, ShapelessRecipeBuilder> builder) {
            final Consumer<IRecipeFactory> orig = this.recipe;
            this.recipe = f -> {
                orig.accept(f);
                // @todo 1.19.3
                f.shapeless(builder.apply(ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItemLike())));
            };
            return this;
        }

        public Builder shapeless(String id, Function<ShapelessRecipeBuilder, ShapelessRecipeBuilder> builder) {
            final Consumer<IRecipeFactory> orig = this.recipe;
            this.recipe = f -> {
                orig.accept(f);
                // @todo 1.19.3
                f.shapeless(id, builder.apply(ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, getItemLike())));
            };
            return this;
        }

        public Dob build() {
            return new Dob(blockSupplier, itemSupplier, entitySupplier,
                    new HashMap<>(codecObjectSupplier), new HashMap<>(glmSupplier),
                    translatedName, new HashMap<>(keyedMessages), new HashMap<>(messages),
                    loot, blockstate, item, blockTags, itemTags, recipe);
        }
    }
}
