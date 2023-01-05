package mcjty.lib.datagen;

import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public record Dob(
        Supplier<? extends Block> blockSupplier,
        Supplier<? extends Item> itemSupplier,
        Consumer<BaseLootTableProvider> loot,
        Consumer<BaseBlockStateProvider> blockstate,
        Consumer<BaseItemModelProvider> item,
        Consumer<ITagFactory> blockTags,
        Consumer<ITagFactory> itemTags,
        Consumer<IRecipeFactory> recipe) {

    public static Builder builder(Supplier<? extends Block> blockSupplier, Supplier<? extends Item> itemSupplier) {
        return new Builder(blockSupplier, itemSupplier);
    }

    public static Builder blockBuilder(Supplier<? extends Block> blockSupplier) {
        return new Builder(blockSupplier, null);
    }

    public static Builder itemBuilder(Supplier<? extends Item> itemSupplier) {
        return new Builder(null, itemSupplier);
    }

    public static class Builder {
        private final Supplier<? extends Block> blockSupplier;
        private final Supplier<? extends Item> itemSupplier;
        private Consumer<BaseLootTableProvider> loot = p -> { };
        private Consumer<BaseBlockStateProvider> blockstate = p -> { };
        private Consumer<BaseItemModelProvider> item = p -> { };
        private Consumer<ITagFactory> blockTags = f -> { };
        private Consumer<ITagFactory> itemTags = f -> { };
        private Consumer<IRecipeFactory> recipe = f -> { };

        public Builder(Supplier<? extends Block> blockSupplier, Supplier<? extends Item> itemSupplier) {
            this.blockSupplier = blockSupplier;
            this.itemSupplier = itemSupplier;
        }

        public Builder loot(Consumer<BaseLootTableProvider> loot) {
            this.loot = loot;
            return this;
        }

        public Builder simpleLoot() {
            this.loot = p -> p.addSimpleTable(blockSupplier.get());
            return this;
        }

        public Builder standardLoot(@SuppressWarnings("rawtypes") RegistryObject be) {
            this.loot = f -> f.addStandardTable(blockSupplier.get(), (BlockEntityType<?>) be.get());
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

        public Builder shaped(ShapedRecipeBuilder builder, String... pattern) {
            this.recipe = f -> f.shaped(builder, pattern);
            return this;
        }

        public Dob build() {
            return new Dob(blockSupplier, itemSupplier, loot, blockstate, item, blockTags, itemTags, recipe);
        }
    }
}
